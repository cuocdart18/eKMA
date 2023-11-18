package com.app.ekma.common.pattern.singleton

object ConnReferenceKey {
    private var key = ""

    operator fun invoke() = key

    fun setData(key: String) = synchronized(this) {
        ConnReferenceKey.key = key
    }

    fun release() {
        key = ""
    }
}