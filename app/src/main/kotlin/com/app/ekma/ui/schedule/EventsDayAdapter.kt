package com.app.ekma.ui.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.R
import com.app.ekma.common.NOTE_TYPE
import com.app.ekma.common.PERIOD_TYPE
import com.app.ekma.common.makeVisible
import com.app.ekma.data.models.Event
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.Period
import com.app.ekma.databinding.ItemEventDayBinding

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
            holder.binding.layoutPeriod.makeVisible()
        } else if (event.type == NOTE_TYPE) {
            val note = event as Note
            holder.binding.note = note
            // set background for expired note
            if (note.getTimeMillis() < System.currentTimeMillis()) {
                holder.binding.layoutNote.setBackgroundResource(R.drawable.bgr_box_of_item_expired_note)
            } else {
                holder.binding.layoutNote.setBackgroundResource(R.drawable.bgr_box_of_item_note)
            }
            // set checkmark state
            holder.binding.cbIsNoteDone.setImageResource(
                if (note.isDone) {
                    R.drawable.checkbox_circle_line
                } else {
                    R.drawable.checkbox_blank_circle_fill
                }
            )
            // set audio enable
            if (note.audioName.isNotEmpty()) {
                holder.binding.imvAudio.setImageResource(R.drawable.mic_outline_white_24dp)
            }
            holder.binding.layoutNote.makeVisible()
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
                                R.drawable.checkbox_circle_line
                            } else {
                                R.drawable.checkbox_blank_circle_fill
                            }
                        )
                    }
                }
            }
        }
    }
}