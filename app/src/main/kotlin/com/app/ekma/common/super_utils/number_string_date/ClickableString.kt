package com.app.ekma.common.super_utils.number_string_date

import android.graphics.Color
import android.graphics.Typeface
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt

fun TextView.setClickInText(colorLink: Int, vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = colorLink
                // toggle below value to enable/disable
                // the underline shown below the clickable text
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        if (startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun String.highlight(
    key: String = this,
    underline: Boolean = false,
    strikeLine: Boolean = false,
    bold: Boolean = false,
    italic: Boolean = false,
    @ColorInt color: Int? = null
): SpannableString {
    val ss = SpannableString(this)

    var mKey = key
    var startIndex = ss.toString().indexOf(key)
    if (startIndex == -1) {
        mKey = this
        startIndex = 0
    }
    ss.setSpan(object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            if (color != null) {
                ds.color = color
                ds.bgColor = Color.TRANSPARENT
            }
        }

        override fun onClick(widget: View) {}
    }, startIndex, startIndex + mKey.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (underline) {
        ss.setSpan(UnderlineSpan(), startIndex, startIndex + mKey.length, 0)
    }
    if (strikeLine) {
        ss.setSpan(StrikethroughSpan(), startIndex, startIndex + mKey.length, 0)
    }
    if (bold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + mKey.length, 0)
    }
    if (italic) {
        ss.setSpan(StyleSpan(Typeface.ITALIC), startIndex, startIndex + mKey.length, 0)
    }
    return ss
}

fun String.highlightBg(
    key: String = this,
    underline: Boolean = false,
    strikeLine: Boolean = false,
    bold: Boolean = false,
    italic: Boolean = false,
    @ColorInt color: Int? = null
): SpannableString {
    val ss = SpannableString(this)

    var mKey = key
    var startIndex = ss.toString().indexOf(key)
    if (startIndex == -1) {
        mKey = this
        startIndex = 0
    }
    ss.setSpan(object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            if (color != null) {
                ds.color = Color.BLACK
                ds.bgColor = color
            }
        }

        override fun onClick(widget: View) {}
    }, startIndex, startIndex + mKey.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (underline) {
        ss.setSpan(UnderlineSpan(), startIndex, startIndex + mKey.length, 0)
    }
    if (strikeLine) {
        ss.setSpan(StrikethroughSpan(), startIndex, startIndex + mKey.length, 0)
    }
    if (bold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + mKey.length, 0)
    }
    if (italic) {
        ss.setSpan(StyleSpan(Typeface.ITALIC), startIndex, startIndex + mKey.length, 0)
    }
    return ss
}

fun EditText.setSpan(
    text: String,
    char1: Char = '[',
    char2: Char = ']',
    color: Int = Color.parseColor("#03E2E1")
) {
    val listIndex1: MutableList<Int> = arrayListOf()
    val listIndex2: MutableList<Int> = arrayListOf()
    for (i in text.indices) {
        if (text[i] == char1) {
            listIndex1.add(i)
        }
        if (text[i] == char2) {
            listIndex2.add(i + 1)
        }
    }
    val stringBuilder = StringBuilder(text)
    val spannableString = SpannableString(stringBuilder.toString())
    if (listIndex1.size == listIndex2.size) {
        for (i in 0 until listIndex1.size) {
            try {
                spannableString.setSpan(
                    BackgroundColorSpan(color),
                    listIndex1[i],
                    listIndex2[i],
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    this.setText(spannableString)
}