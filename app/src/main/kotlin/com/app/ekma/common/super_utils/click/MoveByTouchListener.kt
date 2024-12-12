package com.app.ekma.common.super_utils.click

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class MoveByTouchListener(
    private val parentWidth: Int,
    private val parentHeight: Int
) : View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f
    private var firstEvent: PointF? = null

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                firstEvent = PointF(event.rawX, event.rawY)
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }

            MotionEvent.ACTION_MOVE -> runCatching {
                val newX = (event.rawX + dX).coerceIn(20f, parentWidth - view.width - 20f)
                val newY =
                    (event.rawY + dY).coerceIn(20f, parentHeight - 2 * view.height - 20f)

                view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start()
            }

            MotionEvent.ACTION_UP -> {
                val isClick =
                    abs(event.rawX - firstEvent!!.x) < 10 && abs(event.rawY - firstEvent!!.y) < 10
                if (isClick) {
                    view.performClick()
                } else {
                    val newX = if (event.rawX <= parentWidth / 2) {
                        20f
                    } else {
                        parentWidth - view.width - 20f
                    }
                    view.animate()
                        .x(newX)
                        .setDuration(100)
                        .start()
                }
                firstEvent = null
            }

            else -> return false
        }
        return true
    }
}