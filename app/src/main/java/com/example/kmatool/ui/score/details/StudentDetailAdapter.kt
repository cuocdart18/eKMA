package com.example.kmatool.ui.score.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.databinding.ItemSubjectScoreBinding
import com.example.kmatool.data.models.Score

class StudentDetailAdapter(private val callback: (score: Score) -> Unit) :
    RecyclerView.Adapter<StudentDetailAdapter.StudentDetailViewHolder>() {
    private lateinit var scores: List<Score>
    private lateinit var binding: ItemSubjectScoreBinding

    @SuppressLint("NotifyDataSetChanged")
    fun setScores(scores: List<Score>) {
        this.scores = scores
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentDetailViewHolder {
        binding =
            ItemSubjectScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentDetailViewHolder(binding, callback)
    }

    override fun getItemCount(): Int = scores.size

    override fun onBindViewHolder(holder: StudentDetailViewHolder, position: Int) {
        val score = scores[position]
        // update UI
        // position to define color of row
        score.index = position
        holder.binding.score = score
    }

    inner class StudentDetailViewHolder(
        val binding: ItemSubjectScoreBinding,
        callback: (score: Score) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener() {
                val position = adapterPosition
                callback(scores[position])
            }
        }
    }
}