package com.example.kmatool.ui.schedule.main_scr

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.example.kmatool.databinding.ItemEventDayBinding
import com.example.kmatool.common.NOTE_TYPE
import com.example.kmatool.common.PERIOD_TYPE

class EventsDayAdapter(
    private val callback: (note: Note) -> Unit
) : RecyclerView.Adapter<EventsDayAdapter.PeriodViewHolder>() {
    private lateinit var binding: ItemEventDayBinding
    private lateinit var events: List<Event>

    @SuppressLint("NotifyDataSetChanged")
    fun setData(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodViewHolder {
        binding = ItemEventDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PeriodViewHolder(binding, callback)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        val event = events[position]
        // update UI
        if (event.type == PERIOD_TYPE) {
            holder.binding.layoutPeriod.visibility = View.VISIBLE
            holder.binding.period = event as Period
        } else if (event.type == NOTE_TYPE) {
            holder.binding.layoutNote.visibility = View.VISIBLE
            holder.binding.note = event as Note
        }
    }

    inner class PeriodViewHolder(
        val binding: ItemEventDayBinding,
        private val callback: (note: Note) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val event = events[position]
            if (event.type == NOTE_TYPE) {
                callback(events[position] as Note)
            }
        }
    }
}