package com.app.ekma.common

import com.app.ekma.data.models.Note
import com.app.ekma.data.models.Period
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IScheduleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Data {
    // K = day
    // V = Periods/Notes / day
    var periodsDayMap = mutableMapOf<String, List<Period>>()
    var notesDayMap = mutableMapOf<String, List<Note>>()

    // func
    suspend fun getLocalData(
        noteService: INoteService,
        scheduleService: IScheduleService,
        callback: () -> Unit
    ) {
        coroutineScope {
            val job1 = launch { getLocalPeriodsRuntime(scheduleService) }
            val job2 = launch { getLocalNotesRuntime(noteService) }
            job1.join()
            job2.join()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    suspend fun getLocalPeriodsRuntime(scheduleService: IScheduleService) =
        withContext(Dispatchers.IO) {
            val result = scheduleService.getPeriods()
            if (result is Resource.Success) {
                withContext(Dispatchers.Default) {
                    periodsDayMap =
                        result.data?.groupBy { it.day } as MutableMap<String, List<Period>>
                    // sort periods on a day by startTime
                    periodsDayMap.forEach { (t, u) ->
                        periodsDayMap[t] = u.sortedBy { it.startTime }
                    }
                }
            }
        }

    suspend fun getLocalNotesRuntime(noteService: INoteService) =
        withContext(Dispatchers.IO) {
            val result = noteService.getNotes(ProfileSingleton().studentCode)
            if (result is Resource.Success) {
                withContext(Dispatchers.Default) {
                    notesDayMap =
                        result.data?.groupBy { it.date } as MutableMap<String, List<Note>>
                    // sort notes on a day by day
                    notesDayMap.forEach { (t, u) ->
                        notesDayMap[t] = u.sortedBy { it.time }
                    }
                }
            }
        }
}