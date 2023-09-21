package com.app.ekma.data.data_source.apis.dto


data class ScheduleDto(
    val message: String,
    val periods: List<PeriodDto>
)