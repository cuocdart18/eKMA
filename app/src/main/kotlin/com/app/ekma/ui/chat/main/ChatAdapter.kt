package com.app.ekma.ui.chat.main

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.R
import com.app.ekma.common.IMAGE_MSG
import com.app.ekma.common.TEXT_MSG
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeInVisible
import com.app.ekma.common.makeVisible
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.removeStudentCode
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.data.models.Message
import com.app.ekma.databinding.ItemFriendMessageBinding
import com.app.ekma.databinding.ItemLoadingBinding
import com.app.ekma.databinding.ItemMyMessageBinding
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.storage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.Date

const val SMALL_AVT_W_H = 80

class ChatAdapter(
    private val context: Context,
    private val imageCallback: (imgUrl: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messages: MutableList<Message>
    private var cacheFriendAvtUri: Uri? = null

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
        return if (message.from == ProfileSingleton().studentCode) {
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
            holder.bind(message, position)
        } else if (holder.itemViewType == ITEM_FRIEND_MSG_TYPE) {
            holder as FriendMessageViewHolder
            holder.bind(message, position)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addHeaderLoading() {
        isAddLoading = true
        // fake item
        messages.add(0, Message("", Date(), "", "", 0, mutableListOf()))
    }

    fun removeHeaderLoading() {
        isAddLoading = false
        messages.removeAt(0)
        notifyItemRemoved(0)
    }

    inner class MyMessageViewHolder(
        val binding: ItemMyMessageBinding,
        private val imageCallback: (imgUrl: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, position: Int) {
            when (message.type) {
                TEXT_MSG -> {
                    binding.tvMessage.text = message.content
                    binding.tvMessage.makeVisible()
                    binding.imgContainer.makeGone()
                }

                IMAGE_MSG -> {
                    binding.imvMessage.setOnSingleClickListener { imageCallback(message.content) }
                    Glide.with(context)
                        .load(Uri.parse(message.content))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image_message)
                        .into(binding.imvMessage)
                    binding.imgContainer.makeVisible()
                    binding.tvMessage.makeGone()
                }
            }

            if (message.isLastSeenMessage) runCatching {
                showFriendAvatar(removeStudentCode(message.seen, message.from).first())
            } else {
                binding.imvAvatarSeen.makeGone()
            }

            runCatching {
                if (position == 0) {
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_my_text_msg_open)
                    return
                }
                if (messages[position - 1].from == message.from) {
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_my_text_msg_close)
                    return
                } else {
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_my_text_msg_open)
                }
            }
        }

        private fun showFriendAvatar(friendCode: String) {
            if (cacheFriendAvtUri == null) {
                storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                    .downloadUrl
                    .addOnSuccessListener {
                        cacheFriendAvtUri = it
                        Glide.with(context.applicationContext)
                            .load(cacheFriendAvtUri)
                            .override(SMALL_AVT_W_H, SMALL_AVT_W_H)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(binding.imvAvatarSeen)
                        binding.imvAvatarSeen.makeVisible()
                    }
            } else {
                Glide.with(context.applicationContext)
                    .load(cacheFriendAvtUri)
                    .override(SMALL_AVT_W_H, SMALL_AVT_W_H)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.imvAvatarSeen)
                binding.imvAvatarSeen.makeVisible()
            }
        }
    }

    inner class FriendMessageViewHolder(
        val binding: ItemFriendMessageBinding,
        private val imageCallback: (imgUrl: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, position: Int) {
            when (message.type) {
                TEXT_MSG -> {
                    binding.tvMessage.text = message.content
                    binding.layoutTextMsg.makeVisible()
                    binding.imgContainer.makeGone()
                }

                IMAGE_MSG -> {
                    binding.imvMessage.setOnSingleClickListener { imageCallback(message.content) }
                    Glide.with(context)
                        .load(Uri.parse(message.content))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image_message)
                        .into(binding.imvMessage)
                    binding.imgContainer.makeVisible()
                    binding.layoutTextMsg.makeGone()
                }
            }
            runCatching {
                if (position == 0) {
                    showFriendAvatar(message.from)
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_friend_text_msg_open)
                    return
                }
                if (messages[position - 1].from == message.from) {
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_friend_text_msg_close)
                    binding.imvAvatar.makeInVisible()
                    return
                } else {
                    showFriendAvatar(message.from)
                    binding.tvMessage.setBackgroundResource(R.drawable.bgr_friend_text_msg_open)
                }
            }
        }

        private fun showFriendAvatar(friendCode: String) {
            if (cacheFriendAvtUri == null) {
                storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                    .downloadUrl
                    .addOnSuccessListener {
                        cacheFriendAvtUri = it
                        Glide.with(context.applicationContext)
                            .load(cacheFriendAvtUri)
                            .override(SMALL_AVT_W_H, SMALL_AVT_W_H)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(binding.imvAvatar)
                        binding.imvAvatar.makeVisible()
                    }
            } else {
                Glide.with(context.applicationContext)
                    .load(cacheFriendAvtUri)
                    .override(SMALL_AVT_W_H, SMALL_AVT_W_H)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.imvAvatar)
                binding.imvAvatar.makeVisible()
            }
        }
    }

    inner class LoadingViewHolder(
        val binding: ItemLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)
}