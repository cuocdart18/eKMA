package com.app.ekma.activities.test_event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.app.ekma.R
import com.app.ekma.common.runIntAnimator

class SplashLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paintBorder: Paint
    private val paintBackground: Paint
    private val paintPercent: Paint
    private val strokeWidth by lazy {
        context.resources.getDimension(R.dimen.dp_1)
    }
    private var percent = 0
    private var isRunning = false
    private val path = Path()
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

    init {
        paintBorder = Paint()
        paintBackground = Paint()
        paintPercent = Paint()

        paintBorder.style = Paint.Style.STROKE
        paintBorder.color = Color.parseColor("#69E2FF")
        paintBorder.isAntiAlias = true
        paintBorder.isDither = true
        paintBorder.strokeJoin = Paint.Join.ROUND
        paintBorder.strokeCap = Paint.Cap.ROUND

        paintBackground.style = Paint.Style.FILL
        paintBackground.color = Color.parseColor("#B33A3A3A")
        paintBackground.isAntiAlias = true
        paintBackground.isDither = true
        paintBackground.strokeJoin = Paint.Join.ROUND
        paintBackground.strokeCap = Paint.Cap.ROUND

        paintPercent.style = Paint.Style.FILL
//        paintPercent.color = Color.parseColor("#E7A51B")
        paintPercent.isAntiAlias = true
        paintPercent.isDither = true
        paintPercent.strokeJoin = Paint.Join.ROUND
        paintPercent.strokeCap = Paint.Cap.ROUND
    }

    fun startAnim(duration: Long) {
        isRunning = true
        runIntAnimator(0, 1000, duration) {
            if (isRunning) {
                percent = it
                invalidate()
            }
        }
    }

    fun release() {
        isRunning = false
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val radius = (height.toFloat() / 2f) + strokeWidth * 2
        val strokeBackground = strokeWidth * 2
        val strokePercent = strokeWidth * 4

        paintBorder.strokeWidth = strokeWidth
        paintBackground.strokeWidth = height.toFloat() - strokeBackground
        paintPercent.strokeWidth = height.toFloat() - strokePercent
        val ep = CornerPathEffect(height / 2f)
        paintBackground.setPathEffect(ep)
        paintPercent.setPathEffect(ep)

        path.moveTo(radius - strokeWidth, 0f)
        path.quadTo(0f, 0f, 0f, radius)

        path.moveTo(radius - strokeWidth, 0f)
        path.lineTo(width - radius + strokeWidth, 0f)

        path.moveTo(width - radius + strokeWidth, 0f)
        path.quadTo(width.toFloat(), 0f, width.toFloat(), radius)

        path.moveTo(width.toFloat(), height.toFloat() - radius + strokeWidth)
        path.quadTo(width.toFloat(), height.toFloat(), width - radius, height.toFloat())

        path.moveTo(radius - strokeWidth, height.toFloat())
        path.lineTo(width - radius + strokeWidth, height.toFloat())

        path.moveTo(radius - strokeWidth, height.toFloat())
        path.quadTo(0f, height.toFloat(), 0f, height - radius)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val dy = height / 2f
        canvas.drawLine(dy + strokeWidth / 2, dy, width - strokeWidth / 2 - dy, dy, paintBackground)

        val sp = dy + strokeWidth / 2f
        val ep = width - strokeWidth / 2f - dy
        val wp = ep - sp
        val ex = sp + wp * percent / 1000
        val st = if (ex > ep) ep else ex

        val gradient = LinearGradient(
            0f,
            0f,
            st - sp,
            height.toFloat(),
            colors,
            positions,
            Shader.TileMode.CLAMP
        )

        paintPercent.shader = gradient

        canvas.drawLine(sp, dy, st, dy, paintPercent)

        canvas.drawPath(path, paintBorder)
    }
}