package com.lihan.nichigo.task.presentation.calendar

import java.time.LocalDate

sealed interface CalendarAction {
    data class OnSelectDate(val date: LocalDate) : CalendarAction
    data object OnPreviousMonth : CalendarAction
    data object OnNextMonth : CalendarAction
    data class OnToggleTask(val taskId: Long) : CalendarAction
}
