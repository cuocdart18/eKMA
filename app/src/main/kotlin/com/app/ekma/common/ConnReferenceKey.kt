package com.app.ekma.common

object ConnReferenceKey {
    private var key = ""

    operator fun invoke() = key

    fun setData(key: String) = synchronized(this) {
        this.key = key
    }

    fun release() {
        this.key = ""
    }
}