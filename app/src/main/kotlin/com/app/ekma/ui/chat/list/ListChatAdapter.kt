package com.app.ekma.ui.chat.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.R
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.common.IMAGE_MSG
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.TEXT_MSG
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.common.removeStudentCode
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.models.ChatRoom
import com.app.ekma.databinding.ItemChatRoomBinding
import com.app.ekma.firebase.storage
import com.bumptech.glide.Glide

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
            binding.root.performClick { onClickItem(rooms[adapterPosition].id) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(room: ChatRoom) {
            val myStudentCode = ProfileSingleton().studentCode

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

            if (room.seenMembers.contains(myStudentCode)) {
                binding.tvContent.setTextAppearance(R.style.NormalText)
                binding.tvTime.setTextAppearance(R.style.NormalText)
            } else {
                binding.tvContent.setTextAppearance(R.style.BoldText)
                binding.tvTime.setTextAppearance(R.style.BoldText)
            }

            if (room.isOnline) {
                binding.tvActiveStatus.text = "Online"
            } else {
                binding.tvActiveStatus.text = "Offline"
            }

            // show avatar
            val friendCode = removeStudentCode(room.members, myStudentCode).first()
            storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener { uri ->
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