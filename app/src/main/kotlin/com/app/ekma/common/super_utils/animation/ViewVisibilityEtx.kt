package com.app.ekma.common.super_utils.animation

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.gone(hasAnim: Boolean = false, keepWithIsGone: Boolean = false) {
    if (keepWithIsGone && isGone) return
    gone(hasAnim)
}

fun View.gone(hasAnim: Boolean = false) {
    if (!hasAnim) {
        this.visibility = View.GONE
    } else {
        animateScale(1f, 0f, 1f, 0f, 250) {
            this.visibility = View.GONE
        }
    }
}

fun View.gone(hasAnim: Boolean = false, onEnd: () -> Unit = {}) {
    if (!hasAnim) {
        this.visibility = View.GONE
        onEnd.invoke()
    } else {
        animateScale(1f, 0f, 1f, 0f, 250) {
            this.visibility = View.GONE
            onEnd.invoke()
        }
    }
}

fun View.visible(hasAnim: Boolean = false, keepWithIsGone: Boolean = false) {
    if (keepWithIsGone && isVisible) return
    visible(hasAnim)
}

fun View.visible(hasAnim: Boolean = false) {
    if (!hasAnim) {
        this.visibility = View.VISIBLE
        if (scaleX == 0f || scaleY == 0f || alpha == 0f) {
            setScale(1f)
        }
    } else {
        setScale(0f)
        this.visibility = View.VISIBLE
        animateScale(0f, 1f, 0f, 1f, 250)
    }
}

fun View.onVisible(isVisible: Boolean, isGone: Boolean = true, hasAnim: Boolean = false) {
    if (isVisible) visible(hasAnim) else if (isGone) gone(hasAnim) else invisible(hasAnim)
}

fun View.invisible(hasAnim: Boolean = false, keepWithIsGone: Boolean = false) {
    if (keepWithIsGone && this.visibility == View.INVISIBLE) return
    invisible(hasAnim)
}

fun View.invisible(hasAnim: Boolean = false) {
    if (!hasAnim) {
        this.visibility = View.INVISIBLE
    } else {
        animateScale(1f, 0f, 1f, 0f, 250) {
            this.visibility = View.INVISIBLE
        }
    }
}

fun View.toggled(hasAnim: Boolean = false) {
    if (isVisible) gone(hasAnim) else visible(hasAnim)
}

fun View.setScale(value: Float) {
    scaleX = value
    scaleY = value
    alpha = value
}

fun View.animateScale(
    fromScale: Float,
    toScale: Float,
    fromAlpha: Float,
    toAlpha: Float,
    duration: Long = 100,
    onEnd: () -> Unit = {}
) {
    clearAnimation()
    animate()
        .scaleXBy(fromScale)
        .scaleYBy(fromScale)
        .scaleX(toScale)
        .scaleY(toScale)
        .alphaBy(fromAlpha)
        .alpha(toAlpha)
        .withEndAction(onEnd)
        .setDuration(duration)
        .start()
}