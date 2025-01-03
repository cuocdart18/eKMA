package com.app.ekma.work

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.app.ekma.R
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.common.Data
import com.app.ekma.common.GET_SCHEDULE_ID
import com.app.ekma.common.GET_SCHE_CHANNEL_ID
import com.app.ekma.common.GetPeriodFailingException
import com.app.ekma.common.GetSemesterCodesFailingException
import com.app.ekma.common.Resource
import com.app.ekma.common.pattern.singleton.DownloadScheduleSuccess
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Event
import com.app.ekma.data.models.Period
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.data.models.service.IUserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.EOFException

@HiltWorker
class GetScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userService: IUserService,
    private val scheduleService: IScheduleService,
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = GetScheduleWorker::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {
        // Mark the Worker as important
        try {
            setForeground(createForegroundInfo())
        } catch (e: ForegroundServiceStartNotAllowedException) {
            Log.e(TAG, "doWork: failed with ${e.message}")
            return Result.failure()
        }
        // Do the work here--in this case, get periods.
        try {
            scheduleService.deletePeriods()
            getPeriods()
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

    private suspend fun getPeriods() {
        withContext(Dispatchers.IO) {
            val user = userService.getUser()
            // call API
            val semesterCodes = scheduleService.getSemesterCodes()
            if (semesterCodes is Resource.Success && !semesterCodes.data.isNullOrEmpty()) {
                val fullPeriod = mutableListOf<Period>()
                val getPeriodJobs = mutableListOf<Job>()
                semesterCodes.data.forEach { code ->
                    getPeriodJobs.add(launch {
                        val periods = scheduleService.getPeriods(
                            user.username,
                            user.password,
                            user.hashed,
                            code
                        )
                        if (periods is Resource.Success && periods.data != null) {
                            fullPeriod.addAll(periods.data)
                        } else {
                            throw GetPeriodFailingException(code)
                        }
                    })
                }
                // absolutely request success
                getPeriodJobs.forEach {
                    it.join()
                }
                scheduleService.insertPeriods(fullPeriod)
                setAlarmPeriodsInFirstTime(fullPeriod)
                // update Data runtime
                Data.getLocalPeriodsRuntime(scheduleService)
                withContext(Dispatchers.Main) {
                    DownloadScheduleSuccess.setData(true)
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

    private fun createForegroundInfo(): ForegroundInfo {
        val id = GET_SCHE_CHANNEL_ID
        val title = "Tải xuống toàn bộ lịch học"

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.cloud_download_outline_black_24dp)
            .setOngoing(true)
            .setProgress(100, 0, true)
            .build()

        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                GET_SCHEDULE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(GET_SCHEDULE_ID, notification)
        }
        return foregroundInfo
    }
}