package com.app.ekma.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.APP_EXTERNAL_MEDIA_FOLDER
import com.app.ekma.common.INPUT_DATA_STUDENT_CODE
import com.app.ekma.common.pattern.singleton.DownloadAvatarSuccess
import com.app.ekma.data.data_source.app_data.IDataLocalManager
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
class DownloadAvatarWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataLocalManager: IDataLocalManager
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = DownloadAvatarWorker::class.java.simpleName

    override suspend fun doWork(): Result {

        try {
            downloadAvatar()
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

    private suspend fun downloadAvatar() = withContext(Dispatchers.IO) {
        val myStudentCode = inputData.getString(INPUT_DATA_STUDENT_CODE) ?: throw Exception()
        val file = File(
            applicationContext.getExternalFilesDir("$APP_EXTERNAL_MEDIA_FOLDER/$myStudentCode"),
            AVATAR_FILE
        )

        storage.child("$USERS_DIR/$myStudentCode/$AVATAR_FILE")
            .getFile(file)
            .addOnFailureListener {
                throw it as StorageException
            }
            .await()

        dataLocalManager.saveImgFilePath(file.absolutePath)
        withContext(Dispatchers.Main) {
            DownloadAvatarSuccess.setData(file.absolutePath)
        }
        Log.e(TAG, "downloadAvatar:\nfilePath=${file.absolutePath}")
    }
}