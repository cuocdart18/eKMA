package com.app.ekma.ui.score.details.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.models.Score
import com.app.ekma.databinding.ItemSubjectScoreBinding

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
            binding.root.performClick {
                val position = adapterPosition
                callback(scores[position])
            }
        }
    }
}