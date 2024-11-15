package com.app.ekma.common.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.app.ekma.R
import com.google.android.material.switchmaterial.SwitchMaterial

class NotifyEventSwitchView(
    context: Context,
    attrs: AttributeSet?
) : SwitchMaterial(context, attrs) {

    init {
        isChecked = false
        thumbTintList = ContextCompat.getColorStateList(context, R.color.lab_red)
        trackTintList = ContextCompat.getColorStateList(context, R.color.lab_gray)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        trackTintList = if (checked) {
            ContextCompat.getColorStateList(context, R.color.lab_light_red)
        } else {
            ContextCompat.getColorStateList(context, R.color.lab_gray)
        }
    }
}