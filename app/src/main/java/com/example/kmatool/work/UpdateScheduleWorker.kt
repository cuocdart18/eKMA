package com.example.kmatool.work

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.kmatool.R
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.GetPeriodFailingException
import com.example.kmatool.common.GetSemesterCodesFailingException
import com.example.kmatool.common.Resource
import com.example.kmatool.common.UPDATE_SCHEDULE_ID
import com.example.kmatool.common.UPDATE_SCHE_CHANNEL_ID
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IUserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.EOFException

@HiltWorker
class UpdateScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userService: IUserService,
    private val scheduleService: IScheduleService,
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = UpdateScheduleWorker::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {
        // Mark the Worker as important
        try {
            setForeground(createForegroundInfo())
        } catch (e: ForegroundServiceStartNotAllowedException) {
            Log.e(TAG, "doWork: failed with ${e.message}")
            return Result.failure()
        }
        // Do the work here--in this case, update the periods.
        try {
            updatePeriods()
        } catch (e: EOFException) {
            Log.e(TAG, "doWork: failed with ${e.message}")
            return Result.failure()
        } catch (e: GetPeriodFailingException) {
            Log.e(TAG, "doWork: failed with ${e.message}")
            return Result.failure()
        } catch (e: GetSemesterCodesFailingException) {
            Log.e(TAG, "doWork: failed with ${e.message}")
            return Result.failure()
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private suspend fun updatePeriods() {
        withContext(Dispatchers.IO) {
            val user = userService.getUser()
            // call API
            val semesterCodes = scheduleService.getSemesterCodes()
            if (semesterCodes is Resource.Success && !semesterCodes.data.isNullOrEmpty()) {
                val code = semesterCodes.data[0]
                val periods = scheduleService.getPeriods(
                    user.username,
                    user.password,
                    user.hashed,
                    code
                )
                if (periods is Resource.Success && periods.data != null) {
                    scheduleService.insertPeriods(periods.data)
                    setAlarmPeriodsInFirstTime(periods.data)
                    // update Data runtime
                    Data.getLocalPeriodsRuntime(scheduleService)
                    Log.i(TAG, "updatePeriods: done $id")
                } else {
                    throw GetPeriodFailingException(code)
                }
            } else {
                throw semesterCodes.message?.let { GetSemesterCodesFailingException(it) }!!
            }
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEvents()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(): ForegroundInfo {
        val progress = "Đang xử lí. . ."
        val id = UPDATE_SCHE_CHANNEL_ID
        val title = "Cập nhật lịch học mới nhất"
        val cancel = "Huỷ"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.cloud_download_outline_black_24dp)
            .setOngoing(true)
            .setProgress(100, 0, true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
//            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(UPDATE_SCHEDULE_ID, notification)
    }
}