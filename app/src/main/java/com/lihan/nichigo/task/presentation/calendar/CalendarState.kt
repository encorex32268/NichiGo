package com.lihan.nichigo.task.presentation.calendar

import androidx.compose.runtime.Immutable
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.TaskCard
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class CalendarState(
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val monthlyCompletionStatus: Map<LocalDate, DayCompletionStatus> = emptyMap(),
    val tasksOnSelectedDate: List<TaskCard> = emptyList(),
    val isLoading: Boolean = false
)
