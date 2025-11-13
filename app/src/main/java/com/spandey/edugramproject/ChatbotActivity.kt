package com.spandey.edugramproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.spandey.edugramproject.databinding.ActivityChatbotBinding
import com.spandey.edugramproject.network.AIService
import com.spandey.edugramproject.network.ChatMessage
import com.spandey.edugramproject.network.ChatRequest
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<MessageModel>()
    private val aiService = AIService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        addInitialMessage()

        // Handle send button click
        binding.btnSend.setOnClickListener {
            val userInput = binding.etMessage.text.toString().trim()
            if (userInput.isNotEmpty()) {
                sendMessage(userInput)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun addInitialMessage() {
        messages.add(MessageModel("Hi! I’m your study assistant. Ask me any doubt.", false))
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun sendMessage(userInput: String) {
        // Add user message
        messages.add(MessageModel(userInput, true))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)

        lifecycleScope.launch {
            try {
                // Make API call
                val response = aiService.getChatCompletion(
                    ChatRequest(
                        messages = listOf(
                            ChatMessage("system", "You are a helpful study assistant."),
                            ChatMessage("user", userInput)
                        )
                    )
                )

                // Get AI reply
                val reply = response.choices.firstOrNull()?.message?.content?.trim()
                    ?: "I'm sorry, I couldn’t understand that."

                messages.add(MessageModel(reply, false))
            } catch (e: Exception) {
                messages.add(MessageModel("Error: ${e.message}", false))
            }

            adapter.notifyItemInserted(messages.size - 1)
            binding.recyclerView.scrollToPosition(messages.size - 1)
        }
    }
}
