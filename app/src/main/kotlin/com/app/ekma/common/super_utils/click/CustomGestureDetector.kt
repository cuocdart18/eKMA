package com.app.ekma.common.super_utils.click

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

/*
- How to use

1. define GestureDetector by android
    val gestureDetector = GestureDetector(context, CustomGestureDetector(callback))

2. make a view set on touch listener
    view.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                if (event.action == MotionEvent.ACTION_DOWN) {}
                if (event.action == MotionEvent.ACTION_UP) {}
                ...

**/

internal class CustomGestureDetector(
    private val onLongPressOccurred: (e: MotionEvent?) -> Unit = {},
    private val onSingleTapOccurred: (e: MotionEvent?) -> Unit = {},
    private val onDoubleTapOccurred: (e: MotionEvent?) -> Unit = {}
) : SimpleOnGestureListener() {

    /**
     * On touch down but haven't moved or up yet.
     * Which will help to pause/resume the progress
     * when user long press the view
     */
    override fun onShowPress(e: MotionEvent) {
        onLongPressOccurred(e)
        super.onShowPress(e)
    }

    /**
     * Triggered when tap up event occurs
     */
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return super.onSingleTapUp(e)
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        onDoubleTapOccurred(e)
        return super.onDoubleTapEvent(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onSingleTapOccurred(e)
        return super.onSingleTapConfirmed(e)
    }
}