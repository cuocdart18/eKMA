package com.app.ekma.activities.test_event

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

data class PathCircle(val xPos: Float = 0f, val yPos: Float = 0f)

class DrawingView(
    context: Context?,
    attrs: AttributeSet?
) : View(context, attrs) {

    private val listLineCircle = mutableListOf<MutableList<PathCircle>>()
    private var listCirclePos = mutableListOf<PathCircle>()
    private val paint = Paint()
    private val arcPath = Path()
    private val quadPath = Path()
    private var xPos: Float = 0f
    private var yPos: Float = 0f
    private var isActionUp = false

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        paint.isAntiAlias = true
        paint.isDither = false
        paint.strokeWidth = 15f


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isActionUp = false
                event.assignPos()
            }

            MotionEvent.ACTION_MOVE -> {
                isActionUp = false
                event.assignPos()
            }

            MotionEvent.ACTION_UP -> {
                isActionUp = true
            }
        }
        invalidate()
        return true
    }

    private fun MotionEvent.assignPos() {
        xPos = x
        yPos = y
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isActionUp) {
            arcPath.reset()
            arcPath.moveTo(200f, 600f)
            arcPath.lineTo(200f, 900f)
            canvas.drawPath(arcPath, paint)
        } else {
            arcPath.reset()
            arcPath.moveTo(200f, 600f)
            arcPath.quadTo(xPos, yPos, 200f, 900f)
            canvas.drawPath(arcPath, paint)
        }
    }
}