package com.app.ekma.ui.chat.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.R
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.databinding.ItemSearchUserBinding
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.storage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SearchUserAdapter(
    private val context: Context,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {
    private var miniStudents: List<MiniStudent> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setMiniStudents(miniStudents: List<MiniStudent>) {
        this.miniStudents = miniStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchUserViewHolder {
        val binding =
            ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchUserViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val miniStudent = miniStudents[position]
        holder.bind(miniStudent)
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

        fun bind(miniStudent: MiniStudent) {
            binding.tvName.text = miniStudent.name
            binding.tvId.text = miniStudent.id
            binding.tvClassInSchool.text = miniStudent.classInSchool

            storage.child("$USERS_DIR/${miniStudent.id}/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(context.applicationContext)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(binding.civAvatar)
                }.addOnFailureListener {
                    binding.civAvatar.setImageResource(R.drawable.user)
                }
        }
    }
}