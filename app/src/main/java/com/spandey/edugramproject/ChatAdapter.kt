package com.spandey.edugramproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messageList: MutableList<MessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val USER = 0
        private const val BOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sentByUser) USER else BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == USER) {
            val view = inflater.inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_bot_message, parent, false)
            BotViewHolder(view)
        }
    }

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder) {
            is UserViewHolder -> holder.userMessage.text = message.message
            is BotViewHolder -> holder.botMessage.text = message.message
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessage: TextView = view.findViewById(R.id.tvUserMessage)
    }

    class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val botMessage: TextView = view.findViewById(R.id.tvBotMessage)
    }
}