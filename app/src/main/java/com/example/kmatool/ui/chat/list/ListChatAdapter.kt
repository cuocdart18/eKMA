package com.example.kmatool.ui.chat.list

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kmatool.R
import com.example.kmatool.common.AVATAR_FILE
import com.example.kmatool.common.Data
import com.example.kmatool.common.IMAGE_MSG
import com.example.kmatool.common.TEXT_MSG
import com.example.kmatool.common.USERS_DIR
import com.example.kmatool.common.removeMyStudentCode
import com.example.kmatool.data.models.ChatRoom
import com.example.kmatool.databinding.ItemChatRoomBinding
import com.example.kmatool.firebase.storage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class ListChatAdapter(
    private val context: Context,
    private val onClickItem: (roomId: String) -> Unit
) : RecyclerView.Adapter<ListChatAdapter.ChatRoomViewHolder>() {
    private lateinit var binding: ItemChatRoomBinding
    private lateinit var rooms: List<ChatRoom>

    @SuppressLint("NotifyDataSetChanged")
    fun setChatRooms(rooms: List<ChatRoom>) {
        this.rooms = rooms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListChatAdapter.ChatRoomViewHolder {
        binding = ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomViewHolder(binding, onClickItem)
    }

    override fun onBindViewHolder(holder: ListChatAdapter.ChatRoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int = rooms.size

    inner class ChatRoomViewHolder(
        val binding: ItemChatRoomBinding,
        private val onClickItem: (roomId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClickItem(rooms[adapterPosition].id) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(room: ChatRoom) {
            val myStudentCode = Data.profile.studentCode
            binding.tvRoomName.text = room.name
            if (room.type == TEXT_MSG) {
                if (room.from == myStudentCode) {
                    binding.tvContent.text = "Bạn: ${room.content}"
                } else {
                    binding.tvContent.text = room.content
                }
            } else if (room.type == IMAGE_MSG) {
                if (room.from == myStudentCode) {
                    binding.tvContent.text = "Bạn: Đã gửi một ảnh"
                } else {
                    binding.tvContent.text = "Đã gửi một ảnh"
                }
            }
            binding.tvTime.text = "${room.timestamp.hours}h"
            // show avatar
            val friendCode = removeMyStudentCode(room.members, myStudentCode).first()
            storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    println(uri.toString())
                    Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(binding.civAvatar)
                }.addOnFailureListener {
                    binding.civAvatar.setImageResource(R.drawable.user)
                }
        }
    }
}