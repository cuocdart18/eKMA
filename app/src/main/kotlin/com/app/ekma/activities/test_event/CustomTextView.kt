package com.app.ekma.activities.test_event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.AutoSizeableTextView

@SuppressLint("RestrictedApi")
class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private val TAG = CustomTextView::class.java.simpleName

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_DOWN")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_MOVE")
            }

            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_CANCEL")
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_POINTER_DOWN")
            }

            MotionEvent.ACTION_POINTER_UP -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_POINTER_UP")
            }

            MotionEvent.ACTION_OUTSIDE -> {
                Log.i(TAG, "dispatchTouchEvent: ACTION_OUTSIDE")
            }
        }
        val value = super.dispatchTouchEvent(event)
        Log.i(TAG, "dispatchTouchEvent: return $value")
        return value
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "onTouchEvent: ACTION_DOWN")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "onTouchEvent: ACTION_MOVE")
            }

            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent: ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "onTouchEvent: ACTION_CANCEL")
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i(TAG, "onTouchEvent: ACTION_POINTER_DOWN")
            }

            MotionEvent.ACTION_POINTER_UP -> {
                Log.i(TAG, "onTouchEvent: ACTION_POINTER_UP")
            }

            MotionEvent.ACTION_OUTSIDE -> {
                Log.i(TAG, "onTouchEvent: ACTION_OUTSIDE")
            }
        }
        val value = super.onTouchEvent(event)
        Log.i(TAG, "onTouchEvent: return $value")
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}