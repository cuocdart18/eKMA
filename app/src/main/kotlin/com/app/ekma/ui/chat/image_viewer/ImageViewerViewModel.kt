package com.app.ekma.ui.chat.image_viewer

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.getExtensionFile
import com.app.ekma.common.sdk29AndUp
import com.app.ekma.data.models.Image
import com.app.ekma.firebase.httpsStorageRef
import com.google.firebase.storage.StorageException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = ImageViewerViewModel::class.java.simpleName
    lateinit var imageUrl: String
    private val maxBytesBuffer = 50_000_000L

    private val _showDownloadLoading = MutableStateFlow(false)
    val showDownloadLoading: StateFlow<Boolean>
        get() = _showDownloadLoading.asStateFlow()

    fun downloadImage(
        context: Context,
        callback: () -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            _showDownloadLoading.value = true
            val httpsReference = httpsStorageRef.getReferenceFromUrl(imageUrl)
            httpsReference.metadata.addOnSuccessListener { data ->
                val name = data.name ?: Date().time.toString()
                val type = data.contentType ?: "image/png"
                val dateAdded = Date().time
                val extensionFile = getExtensionFile(type)
                val fileName = "$name.$extensionFile"
                // download image and add to MediaStore provider
                httpsReference.getBytes(maxBytesBuffer).addOnSuccessListener { bytes ->
                    val image = Image(fileName, type, dateAdded, extensionFile, bytes)
                    saveImageToExternalStorage(context, image)
                    _showDownloadLoading.value = false
                    callback()
                }.addOnFailureListener {
                    it as StorageException
                    logError(it.toString())
                    _showDownloadLoading.value = true
                }
            }
        }
    }

    private fun saveImageToExternalStorage(context: Context, image: Image) {
        val imageBitmap = BitmapFactory.decodeByteArray(image.bytes, 0, image.bytes.size)
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, image.name)
            put(MediaStore.Images.Media.DATE_ADDED, image.dateAdded)
            put(MediaStore.Images.Media.MIME_TYPE, image.contentType)
            put(MediaStore.Images.Media.WIDTH, imageBitmap.width)
            put(MediaStore.Images.Media.HEIGHT, imageBitmap.height)
            put(MediaStore.Images.Media.SIZE, image.bytes.size)
        }
        try {
            context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream == null) {
                        logError("Couldn't save image bitmap")
                    } else {
                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                            logError("Couldn't save image bitmap")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}