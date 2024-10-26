package com.app.ekma.common.super_utils.animation

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop

/*
- How to use

1. declare viewTreeObserver here
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        val rootView = androidRoot
        rootView?.getWindowVisibleDisplayFrame(rect)
        val screenHeight = binding.root.height
        val keyboardHeight = screenHeight - rect.bottom + rect.top  // calculate size
        removeListen()
        binding.layoutText.animateMargin(
            marginType = MarginType.BOTTOM,
            duration = 250,
            newMargin = if (keyboardHeight < 0) 0 else keyboardHeight,
            onEnd = ::addListen
        )
    }

2. invoke listen in onResume() - onPause()
    private fun addListen() {
        androidRoot?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun removeListen() {
        androidRoot?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

* */

fun View.animateMargin(
    marginType: MarginType,
    duration: Long,
    newMargin: Int,
    onEnd: () -> Unit = {}
) {
    if (newMargin == getMargin(marginType)) {
        onEnd.invoke()
        return
    }
    val anim = ValueAnimator.ofInt(getMargin(marginType), newMargin)
    anim.addUpdateListener {
        val nm = it.animatedValue as Int
        val layoutParams = this@animateMargin.layoutParams as ViewGroup.MarginLayoutParams
        when (marginType) {
            MarginType.TOP -> layoutParams.topMargin = nm
            MarginType.START -> layoutParams.leftMargin = nm
            MarginType.END -> layoutParams.rightMargin = nm
            MarginType.BOTTOM -> layoutParams.bottomMargin = nm
        }
        this@animateMargin.layoutParams = layoutParams
    }

    anim.doOnEnd {
        onEnd.invoke()
    }

    anim.duration = duration
    anim.start()
}

enum class MarginType {
    TOP, START, END, BOTTOM
}

fun View.getMargin(marginType: MarginType): Int {
    return when (marginType) {
        MarginType.TOP -> marginTop
        MarginType.START -> marginStart
        MarginType.END -> marginEnd
        MarginType.BOTTOM -> marginBottom
    }
}