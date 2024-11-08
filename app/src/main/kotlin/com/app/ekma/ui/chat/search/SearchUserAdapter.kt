package com.app.ekma.ui.chat.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.databinding.ItemSearchUserBinding

class SearchUserAdapter(private val onItemClicked: (String) -> Unit) :
    RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {
    private lateinit var miniStudents: List<MiniStudent>
    private lateinit var binding: ItemSearchUserBinding

    @SuppressLint("NotifyDataSetChanged")
    fun setMiniStudents(miniStudents: List<MiniStudent>) {
        this.miniStudents = miniStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchUserViewHolder {
        binding = ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchUserViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val miniStudent = miniStudents[position]
        holder.binding.miniStudent = miniStudent
    }

    override fun getItemCount(): Int = miniStudents.size

    inner class SearchUserViewHolder(
        val binding: ItemSearchUserBinding,
        private val onItemClicked: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.performClick {
                val position = adapterPosition
                onItemClicked(miniStudents[position].id)
            }
        }
    }
}