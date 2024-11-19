package com.app.ekma.activities.test_event

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.databinding.ActivityEventHandlingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventHandlingActivity : BaseActivity() {
    override val TAG = EventHandlingActivity::class.java.simpleName
    private lateinit var binding: ActivityEventHandlingBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityEventHandlingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loadingView.startAnim(30000L)
    }

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

    override fun onDestroy() {
        super.onDestroy()
        binding.loadingView.release()
    }
}