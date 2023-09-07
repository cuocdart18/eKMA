package com.example.kmatool.ui.chat.main

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kmatool.R
import com.example.kmatool.common.Data
import com.example.kmatool.common.IMAGE_MSG
import com.example.kmatool.common.TEXT_MSG
import com.example.kmatool.common.makeGone
import com.example.kmatool.common.makeVisible
import com.example.kmatool.data.models.Message
import com.example.kmatool.databinding.ItemFriendMessageBinding
import com.example.kmatool.databinding.ItemLoadingBinding
import com.example.kmatool.databinding.ItemMyMessageBinding
import java.util.Date

class ChatAdapter(
    private val context: Context,
    private val imageCallback: (imgUrl: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messages: MutableList<Message>

    private val ITEM_MY_MSG_TYPE = 1
    private val ITEM_FRIEND_MSG_TYPE = 2
    private val LOADING_TYPE = 3

    private var isAddLoading = false

    fun setMessages(messages: MutableList<Message>) {
        this.messages = messages
    }

    override fun getItemViewType(position: Int): Int {
        if ((0 == position) and isAddLoading) {
            return LOADING_TYPE
        }
        val message = messages[position]
        return if (message.from == Data.profile.studentCode) {
            ITEM_MY_MSG_TYPE
        } else {
            ITEM_FRIEND_MSG_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_MY_MSG_TYPE -> {
                val binding =
                    ItemMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyMessageViewHolder(binding, imageCallback)
            }

            ITEM_FRIEND_MSG_TYPE -> {
                val binding =
                    ItemFriendMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                FriendMessageViewHolder(binding, imageCallback)
            }

            else -> {
                val loadBinding =
                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(loadBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.itemViewType == ITEM_MY_MSG_TYPE) {
            holder as MyMessageViewHolder
            holder.bind(message)
        } else if (holder.itemViewType == ITEM_FRIEND_MSG_TYPE) {
            holder as FriendMessageViewHolder
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addHeaderLoading() {
        isAddLoading = true
        // fake item
        messages.add(0, Message(Date(), "", "", 0))
    }

    fun removeHeaderLoading() {
        isAddLoading = false
        messages.removeAt(0)
        notifyItemRemoved(0)
    }

    inner class MyMessageViewHolder(
        val binding: ItemMyMessageBinding,
        private val imageCallback: (imgUrl: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.type == TEXT_MSG) {
                binding.tvMessage.text = message.content
                binding.tvMessage.makeVisible()
                binding.imvMessage.makeGone()
            } else if (message.type == IMAGE_MSG) {
                binding.imvMessage.setOnClickListener { imageCallback(message.content) }
                Glide.with(context)
                    .load(Uri.parse(message.content))
                    .placeholder(R.drawable.default_image_message)
                    .into(binding.imvMessage)
                binding.imvMessage.makeVisible()
                binding.tvMessage.makeGone()
            }
        }
    }

    inner class FriendMessageViewHolder(
        val binding: ItemFriendMessageBinding,
        private val imageCallback: (imgUrl: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.type == TEXT_MSG) {
                binding.tvMessage.text = message.content
                binding.tvMessage.makeVisible()
                binding.imvMessage.makeGone()
            } else if (message.type == IMAGE_MSG) {
                binding.imvMessage.setOnClickListener { imageCallback(message.content) }
                Glide.with(context)
                    .load(Uri.parse(message.content))
                    .placeholder(R.drawable.default_image_message)
                    .into(binding.imvMessage)
                binding.imvMessage.makeVisible()
                binding.tvMessage.makeGone()
            }
        }
    }

    inner class LoadingViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)
}