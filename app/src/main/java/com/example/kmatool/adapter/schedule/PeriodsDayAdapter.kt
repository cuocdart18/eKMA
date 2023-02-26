package com.example.kmatool.adapter.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.databinding.ItemPeriodDayBinding
import com.example.kmatool.models.schedule.Period

class PeriodsDayAdapter : RecyclerView.Adapter<PeriodsDayAdapter.PeriodViewHolder>() {
    private lateinit var binding: ItemPeriodDayBinding
    private lateinit var periods: List<Period>

    @SuppressLint("NotifyDataSetChanged")
    fun setData(periods: List<Period>) {
        this.periods = periods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodViewHolder {
        binding = ItemPeriodDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PeriodViewHolder(binding)
    }

    override fun getItemCount(): Int = periods.size

    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        val period = periods[position]
        // update UI
        holder.binding.period = period
    }

    inner class PeriodViewHolder(val binding: ItemPeriodDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}