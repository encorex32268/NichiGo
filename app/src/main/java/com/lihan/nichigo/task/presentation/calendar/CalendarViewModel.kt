package com.lihan.nichigo.task.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.nichigo.task.domain.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val selectedYearMonth = MutableStateFlow(YearMonth.now())
    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val monthlyStatus = selectedYearMonth.flatMapLatest { ym ->
        taskRepository.getMonthlyCompletionStatus(ym)
    }

    private val tasksOnDate = selectedDate.flatMapLatest { date ->
        taskRepository.getTasksForDate(date)
    }

    val state: StateFlow<CalendarState> = combine(
        selectedYearMonth,
        selectedDate,
        monthlyStatus,
        tasksOnDate
    ) { ym, date, statusMap, tasks ->
        CalendarState(
            selectedYearMonth = ym,
            selectedDate = date,
            monthlyCompletionStatus = statusMap,
            tasksOnSelectedDate = tasks,
            isLoading = false
        )
    }
    .onStart {
        if (!hasLoadedInitialData) {
            hasLoadedInitialData = true
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CalendarState(isLoading = true)
    )

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.OnSelectDate -> {
                selectedDate.value = action.date
                if (YearMonth.from(action.date) != selectedYearMonth.value) {
                    selectedYearMonth.value = YearMonth.from(action.date)
                }
            }
            CalendarAction.OnPreviousMonth -> {
                selectedYearMonth.update { it.minusMonths(1) }
            }
            CalendarAction.OnNextMonth -> {
                selectedYearMonth.update { it.plusMonths(1) }
            }
            is CalendarAction.OnToggleTask -> {
                viewModelScope.launch {
                    taskRepository.toggleTaskCompletion(action.taskId, selectedDate.value)
                }
            }
        }
    }
}
