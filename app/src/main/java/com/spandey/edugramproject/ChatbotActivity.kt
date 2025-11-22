package com.spandey.edugramproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.spandey.edugramproject.databinding.ActivityChatbotBinding
import com.spandey.edugramproject.network.AIService
import com.spandey.edugramproject.network.GeminiContent
import com.spandey.edugramproject.network.GeminiPart
import com.spandey.edugramproject.network.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<MessageModel>()
    private val aiService = AIService.create()
    private val conversationHistory = mutableListOf<GeminiContent>()

    companion object {
        private const val TAG = "ChatbotActivity"
        private const val MAX_RETRIES = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        addInitialMessage()

        binding.btnSend.setOnClickListener {
            val userInput = binding.etMessage.text.toString().trim()
            if (userInput.isNotEmpty()) {
                sendMessage(userInput)
                binding.etMessage.text.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = adapter
    }

    private fun addInitialMessage() {
        messages.add(
            MessageModel(
                "Hi! I'm your Gemini-powered study assistant. Ask me anything!",
                false
            )
        )
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun sendMessage(userInput: String) {
        // Add user message
        messages.add(MessageModel(userInput, true))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)

        // Disable send button while processing
        binding.btnSend.isEnabled = false

        lifecycleScope.launch {
            try {
                // Show typing indicator
                val typingIndex = messages.size
                messages.add(MessageModel("Typing...", false))
                adapter.notifyItemInserted(typingIndex)
                binding.recyclerView.scrollToPosition(messages.size - 1)

                // Add natural delay
                delay((800..1500).random().toLong())

                // Add user message to conversation history
                conversationHistory.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(userInput))
                    )
                )

                val request = GeminiRequest(
                    contents = conversationHistory.toList()
                )

                // Retry logic with exponential backoff
                var lastException: Exception? = null
                var reply: String? = null

                for (attempt in 0..MAX_RETRIES) {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            aiService.getChatCompletion(
                                BuildConfig.GEMINI_API_KEY,
                                request
                            )
                        }

                        reply = response.candidates
                            ?.firstOrNull()
                            ?.content
                            ?.parts
                            ?.firstOrNull()
                            ?.text
                            ?.trim()

                        if (reply.isNullOrEmpty()) {
                            throw Exception("Empty response from AI")
                        }

                        // Success - break retry loop
                        break

                    } catch (e: HttpException) {
                        lastException = e

                        // If rate limited, wait before retry
                        if (e.code() == 429) {
                            if (attempt < MAX_RETRIES) {
                                val waitTime = (5000L * (attempt + 1)) // 5s, 10s, 15s
                                Log.w(TAG, "Rate limited, waiting ${waitTime}ms before retry ${attempt + 1}")

                                // Update typing message
                                messages[typingIndex] = MessageModel(
                                    "Rate limited... retrying in ${waitTime / 1000}s",
                                    false
                                )
                                adapter.notifyItemChanged(typingIndex)

                                delay(waitTime)
                            }
                        } else {
                            // For other HTTP errors, don't retry
                            break
                        }
                    } catch (e: Exception) {
                        lastException = e
                        break
                    }
                }

                // Remove typing indicator
                messages.removeAt(typingIndex)
                adapter.notifyItemRemoved(typingIndex)

                if (reply != null) {
                    // Add AI response to conversation history
                    conversationHistory.add(
                        GeminiContent(
                            role = "model",
                            parts = listOf(GeminiPart(reply))
                        )
                    )

                    messages.add(MessageModel(reply, false))
                    adapter.notifyItemInserted(messages.size - 1)
                } else {
                    // All retries failed
                    throw lastException ?: Exception("Unknown error occurred")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error getting AI response", e)

                // Remove typing indicator if still present
                if (messages.isNotEmpty() && messages.last().message.contains("Typing")) {
                    messages.removeAt(messages.size - 1)
                    adapter.notifyItemRemoved(messages.size)
                }

                val errorMessage = when {
                    e is HttpException && e.code() == 429 ->
                        "⚠️ API rate limit exceeded. Please:\n" +
                                "1. Wait a few minutes and try again\n" +
                                "2. Or get a new API key from ai.google.dev\n" +
                                "3. Or use gemini-1.5-flash-latest model"
                    e is HttpException && e.code() == 404 ->
                        "❌ Model not found. Check AIService.kt and use:\n" +
                                "gemini-1.5-flash-latest (recommended)"
                    e.message?.contains("Unable to resolve host") == true ->
                        "No internet connection. Please check your network."
                    e.message?.contains("timeout") == true ->
                        "Request timed out. Please try again."
                    e is HttpException && e.code() == 401 ->
                        "Invalid API key. Please check your configuration."
                    e is HttpException && e.code() == 403 ->
                        "Access forbidden. Your API key may not have access to this model."
                    e is HttpException && e.code() == 400 ->
                        "Bad request. The message format may be invalid."
                    else ->
                        "Sorry, I encountered an error: ${e.message}"
                }

                messages.add(MessageModel(errorMessage, false))
                adapter.notifyItemInserted(messages.size - 1)

                Toast.makeText(this@ChatbotActivity,
                    if (errorMessage.length > 100) "Error: Check chat" else errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                // Re-enable send button
                binding.btnSend.isEnabled = true
                binding.recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }
}