package com.app.ekma.common.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.app.ekma.R
import com.app.ekma.common.custom_view.ItemBottomView.ACCOUNT
import com.app.ekma.common.custom_view.ItemBottomView.NOTE
import com.app.ekma.common.custom_view.ItemBottomView.SCHEDULE
import com.app.ekma.common.custom_view.ItemBottomView.SCORE
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.LayoutMainBottomBinding

enum class ItemBottomView {
    SCORE,
    SCHEDULE,
    NOTE,
    ACCOUNT
}

class MainBottomView(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs) {
    private val binding = LayoutMainBottomBinding.inflate(LayoutInflater.from(context), this, true)
    private var currentItem: ItemBottomView? = null
    private var onClickItem: (ItemBottomView) -> Unit = {}

    init {
        binding.layoutScore.setOnSingleClickListener {
            setCurrentItem(SCORE)
        }
        binding.layoutSchedule.setOnSingleClickListener {
            setCurrentItem(SCHEDULE)
        }
        binding.layoutNote.setOnSingleClickListener {
            setCurrentItem(NOTE)
        }
        binding.layoutAccount.setOnSingleClickListener {
            setCurrentItem(ACCOUNT)
        }
    }

    fun setInitItem() {
        setCurrentItem(SCORE)
    }

    fun setOnItemSelectedListener(onClickItem: (ItemBottomView) -> Unit = {}) {
        this.onClickItem = onClickItem
    }

    private fun setCurrentItem(itemBottomView: ItemBottomView) {
        resetState()
        when (itemBottomView) {
            SCORE -> {
                binding.layoutScore.setBackgroundResource(R.drawable.bgr_item_main_bottom_view)
                binding.tvScore.visible(hasAnim = true, duration = 600L)
                binding.icScore.setImageResource(R.drawable.ic_score_fill)
            }

            SCHEDULE -> {
                binding.layoutSchedule.setBackgroundResource(R.drawable.bgr_item_main_bottom_view)
                binding.tvSchedule.visible(hasAnim = true, duration = 600L)
                binding.icSchedule.setImageResource(R.drawable.ic_calendar_fill)
            }

            NOTE -> {
                binding.layoutNote.setBackgroundResource(R.drawable.bgr_item_main_bottom_view)
                binding.tvNote.visible(hasAnim = true, duration = 600L)
                binding.icNote.setImageResource(R.drawable.ic_note_fill)
            }

            ACCOUNT -> {
                binding.layoutAccount.setBackgroundResource(R.drawable.bgr_item_main_bottom_view)
                binding.tvAccount.visible(hasAnim = true, duration = 600L)
                binding.icAccount.setImageResource(R.drawable.ic_user_fill)
            }
        }
        currentItem = itemBottomView
        this.onClickItem.invoke(itemBottomView)
    }

    private fun resetState() {
        binding.layoutScore.setBackgroundResource(R.drawable.bgr_item_main_bottom_view_unselect)
        binding.tvScore.gone(hasAnim = currentItem == SCORE, duration = 600L)
        binding.icScore.setImageResource(R.drawable.ic_score_outline)
        binding.layoutSchedule.setBackgroundResource(R.drawable.bgr_item_main_bottom_view_unselect)
        binding.tvSchedule.gone(hasAnim = currentItem == SCHEDULE, duration = 600L)
        binding.icSchedule.setImageResource(R.drawable.ic_calendar_outline)
        binding.layoutNote.setBackgroundResource(R.drawable.bgr_item_main_bottom_view_unselect)
        binding.tvNote.gone(hasAnim = currentItem == NOTE, duration = 600L)
        binding.icNote.setImageResource(R.drawable.ic_note_outline)
        binding.layoutAccount.setBackgroundResource(R.drawable.bgr_item_main_bottom_view_unselect)
        binding.tvAccount.gone(hasAnim = currentItem == ACCOUNT, duration = 600L)
        binding.icAccount.setImageResource(R.drawable.ic_user_outline)
    }
}