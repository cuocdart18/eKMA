package com.app.ekma.common.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

data class MenuItem(
    var position: PointF = PointF(0f, 0f),
    var isSelect: Boolean = false
)

class BackgroundMainBottomView(
    context: Context?,
    attrs: AttributeSet?
) : View(context, attrs) {

    private val path = Path()
    private val bgrPaint = Paint()
    private val colors by lazy {
        intArrayOf(
            Color.parseColor("#FFC3F2"),
            Color.parseColor("#B467FF"),
            Color.parseColor("#7593FF"),
            Color.parseColor("#69E2FF")
        )
    }
    private val positions by lazy {
        floatArrayOf(0f, 0.33f, 0.66f, 1f)
    }
    private var itemPos = listOf<MenuItem>()

    init {
        bgrPaint.style = Paint.Style.FILL
        bgrPaint.color = Color.parseColor("#69E2FF")
        bgrPaint.isAntiAlias = true
        bgrPaint.isDither = true
        bgrPaint.strokeJoin = Paint.Join.ROUND
        bgrPaint.strokeCap = Paint.Cap.ROUND
    }

    fun selectItem(idx: Int) {
        itemPos.forEachIndexed { index, menuItem ->
            menuItem.isSelect = index == idx
        }
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        var ratio = 1
        itemPos.forEachIndexed { _, menuItem ->
            menuItem.position = PointF(width.toFloat() * ratio / 8, 0f)
            ratio += 2
        }

//        val gradient = LinearGradient(
//            0f,
//            0f,
//            width.toFloat(),
//            height.toFloat(),
//            colors,
//            positions,
//            Shader.TileMode.CLAMP
//        )
//        bgrPaint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        path.moveTo(width.toFloat(), height.toFloat() * 1 / 4)
        path.lineTo(width.toFloat(), height.toFloat() * 3 / 4)
        path.quadTo(width.toFloat(), height.toFloat(), width.toFloat() * 15 / 16, height.toFloat())
        path.lineTo(width.toFloat() * 1 / 16, height.toFloat())
        path.quadTo(0f, height.toFloat(), 0f, height.toFloat() * 3 / 4)
        path.lineTo(0f, height.toFloat() * 1 / 4)
        path.quadTo(0f, 0f, width.toFloat() * 1 / 16, 0f)


    }
}