package com.example.kmatool.ui.chat.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.common.Data
import com.example.kmatool.common.makeGone
import com.example.kmatool.common.makeVisible
import com.example.kmatool.data.models.Message
import com.example.kmatool.databinding.ItemMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private lateinit var binding: ItemMessageBinding
    private lateinit var messages: List<Message>

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        if (message.from == Data.profile.studentCode) {
            holder.binding.tvMyMessage.text = message.content
            holder.binding.tvMyMessage.makeVisible()
            holder.binding.tvMessage.makeGone()
        } else {
            holder.binding.tvMessage.text = message.content
            holder.binding.tvMessage.makeVisible()
            holder.binding.tvMyMessage.makeGone()
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)
}