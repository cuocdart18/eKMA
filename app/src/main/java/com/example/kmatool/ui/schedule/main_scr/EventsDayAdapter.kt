package com.example.kmatool.ui.schedule.main_scr

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kmatool.R
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.example.kmatool.databinding.ItemEventDayBinding
import com.example.kmatool.common.NOTE_TYPE
import com.example.kmatool.common.PERIOD_TYPE

class EventsDayAdapter(
    private val callback: (note: Note) -> Unit,
    private val checkboxCallback: (note: Note) -> Unit
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
        return PeriodViewHolder(binding, callback, checkboxCallback)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        val event = events[position]
        // update UI
        if (event.type == PERIOD_TYPE) {
            holder.binding.period = event as Period
            holder.binding.layoutPeriod.visibility = View.VISIBLE
        } else if (event.type == NOTE_TYPE) {
            val note = event as Note
            holder.binding.note = note
            if (note.getTimeMillis() < System.currentTimeMillis()) {
                holder.binding.layoutNote.setBackgroundResource(R.drawable.bgr_box_of_item_expired_note)
            } else {
                holder.binding.layoutNote.setBackgroundResource(R.drawable.bgr_box_of_item_note)
            }
            holder.binding.cbIsNoteDone.setImageResource(
                if (note.isDone) {
                    R.drawable.checkmark_circle_outline_28dp_blue
                } else {
                    R.drawable.ellipse_outline_28dp_white
                }
            )
            holder.binding.layoutNote.visibility = View.VISIBLE
        }
    }

    inner class PeriodViewHolder(
        val binding: ItemEventDayBinding,
        private val callback: (note: Note) -> Unit,
        private val checkboxCallback: (note: Note) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.layoutNoteContent.setOnClickListener(this)
            binding.cbIsNoteDone.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            val event = events[position]
            if (event.type == NOTE_TYPE) {
                when (v.id) {
                    binding.layoutNoteContent.id -> {
                        callback(events[position] as Note)
                    }

                    binding.cbIsNoteDone.id -> {
                        val note = events[position] as Note
                        checkboxCallback(note)
                        binding.cbIsNoteDone.setImageResource(
                            if (note.isDone) {
                                R.drawable.checkmark_circle_outline_28dp_blue
                            } else {
                                R.drawable.ellipse_outline_28dp_white
                            }
                        )
                    }
                }
            }
        }
    }
}