package com.app.ekma.data.models

class Image(
    val name: String,
    val contentType: String,
    val dateAdded: Long,
    val extensionFile: String,
    val bytes: ByteArray,
)
