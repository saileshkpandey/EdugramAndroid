package com.spandey.edugramproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messageList: MutableList<MessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER = 0
    private val BOT = 1

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sentByUser) USER else BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bot_message, parent, false)
            BotViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is UserViewHolder) {
            holder.userMessage.text = message.message
        } else if (holder is BotViewHolder) {
            holder.botMessage.text = message.message
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessage: TextView = view.findViewById(R.id.tvUserMessage)
    }

    class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val botMessage: TextView = view.findViewById(R.id.tvBotMessage)
    }
}
