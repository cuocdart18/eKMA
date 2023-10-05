package com.app.ekma.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.INPUT_DATA_IMAGE_URI
import com.app.ekma.common.INPUT_DATA_STUDENT_CODE
import com.app.ekma.firebase.AVATAR_FILE
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
class UploadAvatarWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = UploadAvatarWorker::class.java.simpleName

    override suspend fun doWork(): Result {

        try {
            uploadAvatar()
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

    private suspend fun uploadAvatar() = withContext(Dispatchers.IO) {
        val myStudentCode = inputData.getString(INPUT_DATA_STUDENT_CODE) ?: throw Exception()
        val filePath = inputData.getString(INPUT_DATA_IMAGE_URI) ?: throw Exception()
        val file = File(filePath)

        storage.child("$USERS_DIR/$myStudentCode/$AVATAR_FILE")
            .putBytes(file.readBytes())
            .addOnFailureListener {
                throw it as StorageException
            }
            .await()
        Log.e(TAG, "uploadAvatar:\nmyStudentCode=$myStudentCode\nfilePath=$filePath")
    }
}