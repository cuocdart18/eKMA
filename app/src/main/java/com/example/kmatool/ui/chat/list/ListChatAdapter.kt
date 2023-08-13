package com.example.kmatool.ui.chat.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.common.makeGone
import com.example.kmatool.data.models.ChatRoom
import com.example.kmatool.databinding.ItemChatRoomBinding

class ListChatAdapter(
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
        holder.binding.tvRoomName.text = room.name
    }

    override fun getItemCount(): Int = rooms.size

    inner class ChatRoomViewHolder(
        val binding: ItemChatRoomBinding,
        private val onClickItem: (roomId: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClickItem(rooms[adapterPosition].id) }
        }
    }
}