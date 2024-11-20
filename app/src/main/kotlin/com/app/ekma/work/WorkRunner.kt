package com.app.ekma.work

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.app.ekma.common.DELETE_AUDIO_NOTE_WORKER_TAG
import com.app.ekma.common.DOWNLOAD_AUDIO_NOTES_WORKER_TAG
import com.app.ekma.common.DOWNLOAD_AVATAR_WORKER_TAG
import com.app.ekma.common.GET_SCHEDULE_WORKER_TAG
import com.app.ekma.common.INPUT_DATA_AUDIO_NOTE_NAME
import com.app.ekma.common.INPUT_DATA_IMAGE_URI
import com.app.ekma.common.INPUT_DATA_STUDENT_CODE
import com.app.ekma.common.UNIQUE_DELETE_AUDIO_NOTE_WORK_NAME
import com.app.ekma.common.UNIQUE_DOWNLOAD_AUDIO_NOTES_WORK_NAME
import com.app.ekma.common.UNIQUE_DOWNLOAD_AVATAR_WORK_NAME
import com.app.ekma.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.app.ekma.common.UNIQUE_UPLOAD_AUDIO_NOTE_WORK_NAME
import com.app.ekma.common.UNIQUE_UPLOAD_AVATAR_WORK_NAME
import com.app.ekma.common.UPLOAD_AUDIO_NOTE_WORKER_TAG
import com.app.ekma.common.UPLOAD_AVATAR_WORKER_TAG
import java.util.concurrent.TimeUnit

object WorkRunner {

    fun runGetScheduleWorker(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val getScheduleWorkRequest =
            OneTimeWorkRequestBuilder<GetScheduleWorker>()
                .addTag(GET_SCHEDULE_WORKER_TAG)
                .setConstraints(constraints)
                .build()
        workManager
            .enqueueUniqueWork(
                UNIQUE_GET_SCHEDULE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                getScheduleWorkRequest
            )
    }

    fun runUploadAvatarWorker(
        workManager: WorkManager,
        myStudentCode: String,
        uriString: String
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadAvatarWorkRequest =
            OneTimeWorkRequestBuilder<UploadAvatarWorker>()
                .addTag(UPLOAD_AVATAR_WORKER_TAG)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10_000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        INPUT_DATA_STUDENT_CODE to myStudentCode,
                        INPUT_DATA_IMAGE_URI to uriString
                    )
                )
                .build()
        val workName = "${UNIQUE_UPLOAD_AVATAR_WORK_NAME}_$myStudentCode"
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                uploadAvatarWorkRequest
            )
    }

    fun runDownloadAvatarWorker(
        workManager: WorkManager,
        myStudentCode: String
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val downloadAvatarWorkRequest =
            OneTimeWorkRequestBuilder<DownloadAvatarWorker>()
                .addTag(DOWNLOAD_AVATAR_WORKER_TAG)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10_000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        INPUT_DATA_STUDENT_CODE to myStudentCode
                    )
                )
                .build()
        val workName = "${UNIQUE_DOWNLOAD_AVATAR_WORK_NAME}_$myStudentCode"
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                downloadAvatarWorkRequest
            )
    }

    fun runUploadAudioNoteWorker(
        workManager: WorkManager,
        myStudentCode: String,
        fileName: String
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadAudioNoteWorkRequest =
            OneTimeWorkRequestBuilder<UploadAudioNoteWorker>()
                .addTag(UPLOAD_AUDIO_NOTE_WORKER_TAG)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10_000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        INPUT_DATA_STUDENT_CODE to myStudentCode,
                        INPUT_DATA_AUDIO_NOTE_NAME to fileName
                    )
                )
                .build()
        val workName = "${UNIQUE_UPLOAD_AUDIO_NOTE_WORK_NAME}_$fileName"
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                uploadAudioNoteWorkRequest
            )
    }

    fun runDownloadAudioNotesWorker(
        workManager: WorkManager,
        myStudentCode: String
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val downloadAudioNotesWorkRequest =
            OneTimeWorkRequestBuilder<DownloadAudioNoteWorker>()
                .addTag(DOWNLOAD_AUDIO_NOTES_WORKER_TAG)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10_000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        INPUT_DATA_STUDENT_CODE to myStudentCode
                    )
                )
                .build()
        val workName = "${UNIQUE_DOWNLOAD_AUDIO_NOTES_WORK_NAME}_$myStudentCode"
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                downloadAudioNotesWorkRequest
            )
    }

    fun runDeleteAudioNoteWorker(
        workManager: WorkManager,
        myStudentCode: String,
        fileName: String
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadDeleteNoteWorkRequest =
            OneTimeWorkRequestBuilder<DeleteAudioNoteWorker>()
                .addTag(DELETE_AUDIO_NOTE_WORKER_TAG)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10_000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        INPUT_DATA_STUDENT_CODE to myStudentCode,
                        INPUT_DATA_AUDIO_NOTE_NAME to fileName
                    )
                )
                .build()
        val workName = "${UNIQUE_DELETE_AUDIO_NOTE_WORK_NAME}_$fileName"
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                uploadDeleteNoteWorkRequest
            )
    }
}