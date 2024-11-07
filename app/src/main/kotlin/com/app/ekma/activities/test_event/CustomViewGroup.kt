package com.app.ekma.activities.test_event

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.app.ekma.databinding.LayoutCustomViewGroupBinding

@SuppressLint("SetTextI18n")
class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val TAG = CustomViewGroup::class.java.simpleName
    private val binding =
        LayoutCustomViewGroupBinding.inflate(LayoutInflater.from(context), this, true)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
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
        val value = super.dispatchTouchEvent(ev)
        Log.i(TAG, "dispatchTouchEvent: return $value")
        return value
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_DOWN")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_MOVE")
            }

            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_CANCEL")
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_POINTER_DOWN")
            }

            MotionEvent.ACTION_POINTER_UP -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_POINTER_UP")
            }

            MotionEvent.ACTION_OUTSIDE -> {
                Log.i(TAG, "onInterceptTouchEvent: ACTION_OUTSIDE")
            }
        }
        val value = super.onInterceptTouchEvent(ev)
        Log.i(TAG, "onInterceptTouchEvent: return $value")
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
        return value
    }
}