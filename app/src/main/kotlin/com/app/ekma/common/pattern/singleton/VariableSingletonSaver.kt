package com.app.ekma.common.pattern.singleton

import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

object ClickedDay {
    private var clickedDay = CalendarDay(LocalDate.now(), DayPosition.MonthDate)

    operator fun invoke() = clickedDay

    fun setData(day: CalendarDay) = synchronized(this) {
        clickedDay = day
    }

    fun release() {
        clickedDay = CalendarDay(LocalDate.now(), DayPosition.MonthDate)
    }
}

object CurrentEventsRefresher {
    private var refreshEvent = MutableLiveData(false)

    operator fun invoke() = refreshEvent

    fun setData(value: Boolean) = synchronized(this) {
        refreshEvent.value = value
    }

    suspend fun release() = withContext(Dispatchers.Main) {
        refreshEvent.value = false
    }
}

object MainBottomNavigation {
    private var hideBottomNav = MutableLiveData(false)

    operator fun invoke() = hideBottomNav

    fun setData(value: Boolean) = synchronized(this) {
        hideBottomNav.value = value
    }

    suspend fun release() = withContext(Dispatchers.Main) {
        hideBottomNav.value = false
    }
}

object BusyCalling {
    private var isBusy = false

    operator fun invoke() = isBusy

    fun setData(value: Boolean) = synchronized(this) {
        isBusy = value
    }

    fun release() {
        isBusy = false
    }
}

object CallingOperationResponse {
    private var operation = MutableLiveData("")

    operator fun invoke() = operation

    fun setData(value: String) = synchronized(this) {
        operation.value = value
    }

    fun release() {
        operation.value = ""
    }
}
