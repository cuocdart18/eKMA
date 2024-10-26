package com.app.ekma.common.super_utils.animation

import android.widget.TextView

class TextTyper {
    companion object {
        fun target(tv: TextView): TyperConfig {
            val origin = tv.tag
            if (origin != null) {
                val tcf = origin as TyperConfig
                tcf.stop()
            }
            tv.text = ""
            return TyperConfig(tv)
        }
    }
}

class TyperConfig {
    private val mTv: TextView
    private var mCurrentSpeed: Int = 25 //default 50
    internal var mActionHead: ActionItem? = null
    internal var mLastAction: ActionItem? = mActionHead
    private var isStoped: Boolean = false
    private var isTicToc: Boolean = true
    private var textNotTyping: String = ""
    private var stopTicToc = false

    constructor(tv: TextView) {
        tv.tag = this
        mTv = tv


    }

    fun appendItem(item: ActionItem) {
        if (mActionHead == null) {
            mActionHead = item
            mLastAction = mActionHead
        }
        mLastAction?.next = item
        mLastAction = item
    }

    fun speed(speed: Int): TyperConfig {
        val item = ActionItem(type = Action.SPEED, value = speed)
        appendItem(item)
        return this
    }

    fun input(text: String): TyperConfig {
        val item = ActionItem(type = Action.ADD_TXT, value = text)
        appendItem(item)
        stopTicToc(true)
        return this
    }

    fun inputWithText(text: String, textNotTyping: String, stop: Boolean = false): TyperConfig {
        this.textNotTyping = textNotTyping
        val item = ActionItem(type = Action.ADD_TXT, value = text)
        appendItem(item)
        stopTicToc(stop)
        return this
    }

    fun delete(del: Int): TyperConfig {
        val item = ActionItem(type = Action.DELETE, value = del)
        appendItem(item)
        return this
    }

    fun sleep(sleep: Int): TyperConfig {
        val item = ActionItem(type = Action.SLEEP, value = sleep)
        appendItem(item)
        return this
    }

    fun start(callback: Next? = null) {
        launchAction(mActionHead, callback)
    }

    fun stop() {
        isStoped = true
    }

    fun stopTicToc(stop: Boolean) {
        stopTicToc = stop
    }

    private fun add(text: String, next: Next, speed: Int = mCurrentSpeed) {
        _add(text, 0, speed, object : Next {
            override fun next() {
                next.next()
            }

            override fun end() {
                next.end()
            }

            override fun continueTyping() {
                next.continueTyping()
            }
        })

    }

    private fun _add(text: String, i: Int, speed: Int, next: Next) {
        if (isStoped) return
        if (i < text.length) {
//            mTv.postDelayed({
//                _del(1,0, next)
//            }, 0.toLong())
            if (mTv.text.isNotEmpty()) {
                mTv.text = mTv.text.substring(
                    0,
                    mTv.text.length - 1
                )
            }
            if (i == 0) {
                mTv.append(textNotTyping + text.substring(i until i + 1) + "|")
            } else {
                mTv.append(text.substring(i until i + 1) + "|")
            }

            mTv.postDelayed({
                _add(text, i + 1, speed, next)
            }, speed.toLong())
            next.next()
        } else {
            if (text.isNotEmpty() && i == text.length) {
//                mTv.text = mTv.text.substring(
//                    0,
//                    mTv.text.length - 1
//                )
//                next.end()

                if (isTicToc) {
                    mTv.text = mTv.text.substring(
                        0,
                        mTv.text.length - 1
                    )
                    if (stopTicToc) {
                        stop()
                        next.end()
                    }
                    next.continueTyping()
                } else {
                    mTv.append("|")
                }
                isTicToc = !isTicToc
                mTv.postDelayed({
                    _add(text, i, 250, next)
                }, speed.toLong())
            } else {
                next.next()

            }
        }
    }

    private fun del(delCount: Int, next: Next, speed: Int = mCurrentSpeed) {
        _del(delCount, speed, object : Next {
            override fun next() {
                next.next()
            }

            override fun end() {

            }

            override fun continueTyping() {
            }
        })

    }

    private fun _del(i: Int, speed: Int, next: Next) {
        if (i > 0 && !isStoped) {
            mTv.text = mTv.text.substring(
                0,
                mTv.text.length - 1
            )
            mTv.postDelayed({
                _del(i - 1, speed, next)
            }, speed.toLong())
        } else {
            next.next()
        }
    }


    private fun launchAction(item: ActionItem?, callback: Next?) {
        if (item == null) {
            callback?.next()
            return
        }
        val next = object : Next {
            override fun next() {
                launchAction(item.next, callback)
            }

            override fun end() {
                callback?.end()
            }

            override fun continueTyping() {
                callback?.continueTyping()
            }
        }
        if (isStoped) return
        when (item.type) {
            Action.SPEED -> {
                mCurrentSpeed = item.value as Int
                next.next()
            }

            Action.ADD_TXT -> add(item.value as String, next = next)
            Action.SLEEP -> {
                mTv.postDelayed({ next.next() }, (item.value as Int).toLong())
            }

            Action.DELETE -> {
                del(item.value as Int, next)
            }

        }
    }

    enum class Action {
        ADD_TXT, SLEEP, DELETE, SPEED
    }

    interface Next {
        fun next()
        fun end()
        fun continueTyping()
    }

    data class ActionItem(val type: Action, val value: Any) {
        var next: ActionItem? = null
    }
}