package com.lihan.nichigo.task.domain.model

enum class PeriodType {
    DAILY,
    INTERVAL,
    WEEKLY
}

data class Period(
    val id: Long = 0,
    val type: PeriodType = PeriodType.DAILY,
    val intervalDays: Int = 1,
    val daysOfWeek: List<Int> = emptyList(), // 1 for Monday, ..., 7 for Sunday
    val displayTitle: String = "每日"
)
