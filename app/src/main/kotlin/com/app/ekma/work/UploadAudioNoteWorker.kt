package com.app.ekma.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.APP_EXTERNAL_MEDIA_FOLDER
import com.app.ekma.common.EXTERNAL_AUDIO_FOLDER
import com.app.ekma.common.INPUT_DATA_AUDIO_NOTE_NAME
import com.app.ekma.common.INPUT_DATA_STUDENT_CODE
import com.app.ekma.firebase.AUDIO_NOTES_DIR
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.storage
import com.google.firebase.storage.StorageException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

@HiltWorker
class UploadAudioNoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = UploadAudioNoteWorker::class.java.simpleName

    override suspend fun doWork(): Result {

        try {
            uploadAudioNote()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        } catch (e: StorageException) {
            e.printStackTrace()
            return Result.failure()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    private suspend fun uploadAudioNote() = withContext(Dispatchers.IO) {
        val myStudentCode = inputData.getString(INPUT_DATA_STUDENT_CODE) ?: throw Exception()
        val fileName = inputData.getString(INPUT_DATA_AUDIO_NOTE_NAME) ?: throw Exception()
        val file = File(
            applicationContext.getExternalFilesDir("$APP_EXTERNAL_MEDIA_FOLDER/$myStudentCode/$EXTERNAL_AUDIO_FOLDER"),
            fileName
        )

        storage
            .child("$USERS_DIR/$myStudentCode/$AUDIO_NOTES_DIR/$fileName")
            .putBytes(file.readBytes())
            .addOnFailureListener {
                throw it as StorageException
            }
            .await()
        Log.e(
            TAG,
            "uploadAudioNote:\nmyStudentCode=$myStudentCode\nfileName=$fileName"
        )
    }
}