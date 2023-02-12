package com.example.kmatool.adapter.score

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.databinding.ItemSearchDataBinding
import com.example.kmatool.models.score.MiniStudent

class SearchDataAdapter(private val onItemClicked: (miniStudent: MiniStudent) -> Unit) :
    RecyclerView.Adapter<SearchDataAdapter.SearchDataViewHolder>() {
    private lateinit var miniStudents: List<MiniStudent>
    private lateinit var binding: ItemSearchDataBinding

    @SuppressLint("NotifyDataSetChanged")
    fun setMiniStudents(miniStudents: List<MiniStudent>) {
        this.miniStudents = miniStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchDataViewHolder {
        binding = ItemSearchDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchDataViewHolder(binding, onItemClicked)
    }

    override fun getItemCount(): Int = miniStudents.size

    override fun onBindViewHolder(holder: SearchDataViewHolder, position: Int) {
        val miniStudent = miniStudents[position]
        // index for background item is Gray : Black
        miniStudent.index = position
        // update UI
        holder.binding.miniStudent = miniStudent
    }

    inner class SearchDataViewHolder(
        val binding: ItemSearchDataBinding,
        private val onItemClicked: (miniStudent: MiniStudent) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            onItemClicked(miniStudents[position])
        }
    }
}