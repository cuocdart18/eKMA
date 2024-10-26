package com.app.ekma.common.super_utils.bitmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.min
import kotlin.math.roundToInt

fun getColorByPointF(imgBmp: Bitmap, pointF: PointF): Int {
    return imgBmp.getPixel(pointF.x.toInt(), pointF.y.toInt())
}

fun getColorByPointF(imgBmp: Bitmap, x: Float, y: Float): Int =
    imgBmp.getPixel(x.toInt(), y.toInt())

const val FLIP_VERTICAL = 1
const val FLIP_HORIZONTAL = 2
fun flipBmp(src: Bitmap, type: Int): Bitmap {
    val matrix = Matrix()
    if (type == FLIP_VERTICAL) {
        matrix.preScale(1.0f, -1.0f)
    } else if (type == FLIP_HORIZONTAL) {
        matrix.preScale(-1.0f, 1.0f)
    }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

fun Bitmap.toARGBBitmap(): Bitmap {
    return copy(Bitmap.Config.ARGB_8888, true)
}

fun Bitmap.getResizedBitmap(newHeight: Int): Bitmap {
    val width = this.width
    val height = this.height
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleHeight, scaleHeight)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

fun Bitmap.resizeAndCropCenter(size: Int, recycle: Boolean): Bitmap {
    val w = this.width
    val h = this.height
    if (w == size && h == size) return this
    val scale = size.toFloat() / min(w, h)
    val target = Bitmap.createBitmap(size, size, getConfig(this))
    val width = (scale * this.width).roundToInt()
    val height = (scale * this.height).roundToInt()
    val canvas = Canvas(target)
    canvas.translate((size - width) / 2f, (size - height) / 2f)
    canvas.scale(scale, scale)
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    canvas.drawBitmap(this, 0f, 0f, paint)
    if (recycle) this.recycle()
    return target
}

private fun getConfig(bitmap: Bitmap): Bitmap.Config {
    var config = bitmap.config
    if (config == null) {
        config = Bitmap.Config.ARGB_8888
    }
    return config
}

fun Bitmap.getHorizontalFlipBitmapImage(): Bitmap {
    val matrix = Matrix()
    matrix.preScale(-1.0f, 1.0f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Bitmap.replaceColor(fromColor: Int, targetColor: Int): Bitmap {
    // Source image size
    val width = this.width
    val height = this.height
    val pixels = IntArray(width * height)
    //get pixels
    this.getPixels(pixels, 0, width, 0, 0, width, height)
    for (x in pixels.indices) {
        pixels[x] = if (pixels[x] == fromColor) targetColor else pixels[x]
    }
    // create result bitmap output
    val result = Bitmap.createBitmap(width, height, this.config)
    //set pixels
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

fun Bitmap.darkenText(contrast: Float): Bitmap {
    val cm = ColorMatrix()
    val scale = contrast + 1f
    val translate = (-.5f * scale + .5f) * 255f
    cm.set(
        floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        )
    )
    val ret = Bitmap.createBitmap(this.width, this.height, this.config)
    val canvas = Canvas(ret)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return ret
}

fun Bitmap.getDominantColor(): Int {
    val width = width
    val height = height
    val size = width * height
    val pixels = IntArray(size)
    getPixels(pixels, 0, width, 0, 0, width, height)
    var color: Int
    var r = 0
    var g = 0
    var b = 0
    var a: Int
    var count = 0
    for (i in pixels.indices) {
        color = pixels[i]
        a = Color.alpha(color)
        if (a > 0) {
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
            count++
        }
    }
    r /= count
    g /= count
    b /= count
    r = r shl 16 and 0x00FF0000
    g = g shl 8 and 0x0000FF00
    b = b and 0x000000FF
    color = -0x1000000 or r or g or b
    return color
}

