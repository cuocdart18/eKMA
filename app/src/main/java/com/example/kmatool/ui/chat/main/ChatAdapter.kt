package com.example.kmatool.ui.chat.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.common.Data
import com.example.kmatool.common.makeGone
import com.example.kmatool.common.makeVisible
import com.example.kmatool.data.models.Message
import com.example.kmatool.databinding.ItemLoadingBinding
import com.example.kmatool.databinding.ItemMessageBinding
import java.util.Date

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemMessageBinding
    private lateinit var loadBinding: ItemLoadingBinding
    private lateinit var messages: MutableList<Message>

    private val ITEM_TYPE = 1
    private val LOADING_TYPE = 2
    private var isAddLoading = false

    fun setMessages(messages: MutableList<Message>) {
        this.messages = messages
    }

    override fun getItemViewType(position: Int): Int {
        if ((0 == position) and isAddLoading) {
            return LOADING_TYPE
        }
        return ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE) {
            binding =
                ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ChatViewHolder(binding)
        } else {
            loadBinding =
                ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(loadBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == ITEM_TYPE) {
            holder as ChatViewHolder
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
    }

    override fun getItemCount(): Int = messages.size

    fun addHeaderLoading() {
        isAddLoading = true
        // fake item
        messages.add(0, Message(Date(), "", ""))
    }

    fun removeHeaderLoading() {
        isAddLoading = false
        messages.removeAt(0)
        notifyItemRemoved(0)
    }

    inner class ChatViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)
}