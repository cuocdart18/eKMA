package com.app.ekma.common.super_utils.timer

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeoutManager(private val builder: Builder) {
    private var isLoaded = false

    init {
        builder.scope?.launch {
            Log.d("TimeoutManager", "starting")
            builder.action.invoke(this@TimeoutManager)

            launch {
                delay(builder.timeout)

                if(!builder.skip) {
                    onLoaded()
                }
            }
        }
    }

    fun onLoaded() {
        if(!isLoaded) {
            isLoaded = true
            pushNext()
        }
    }

    private fun pushNext() {
        if(isLoaded) {
            builder.actionNext.invoke()
        }
    }

    class Builder {
        var action: (TimeoutManager) -> Unit = {}
        var actionNext: () -> Unit = {}
        var timeout: Long = 0L
        var skip: Boolean = false
        var scope: CoroutineScope? = null

        fun withNext(actionNext: () -> Unit): Builder {
            this.actionNext = actionNext
            return this
        }

        fun withAction(action: (TimeoutManager) -> Unit): Builder {
            this.action = action
            return this
        }

        fun withTimeout(timeout: Long): Builder {
            this.timeout = timeout
            return this
        }

        fun withSkip(isSkip: Boolean): Builder {
            this.skip = isSkip
            return this
        }

        fun withScope(scope: LifecycleOwner): Builder {
            this.scope = scope.lifecycleScope
            return this
        }

        fun withScope(scope: CoroutineScope): Builder {
            this.scope = scope
            return this
        }

        fun withScope(scope: LifecycleCoroutineScope): Builder {
            this.scope = scope
            return this
        }

        fun build(): TimeoutManager {
            return TimeoutManager(this)
        }
    }
}