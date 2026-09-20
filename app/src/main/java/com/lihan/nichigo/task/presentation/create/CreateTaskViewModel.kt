package com.lihan.nichigo.task.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.nichigo.core.domain.util.onError
import com.lihan.nichigo.core.domain.util.onSuccess
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.repository.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CreateTaskEvent {
    data object TaskCreatedSuccess : CreateTaskEvent
}

class CreateTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateTaskState())
    private val availableTags = taskRepository.getAllHashTags()

    val state: StateFlow<CreateTaskState> = combine(_state, availableTags) { internalState, tags ->
        internalState.copy(availableTags = tags)
    }.onStart {
        if (!hasLoadedInitialData) {
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CreateTaskState()
    )

    private val _eventChannel = Channel<CreateTaskEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: CreateTaskAction) {
        when (action) {
            is CreateTaskAction.OnTitleChange -> {
                if (action.title.length <= 30) {
                    _state.update { it.copy(title = action.title) }
                }
            }
            is CreateTaskAction.OnPeriodTypeChange -> {
                _state.update { it.copy(periodType = action.type) }
            }
            is CreateTaskAction.OnIntervalDaysChange -> {
                _state.update { it.copy(intervalDays = action.days.coerceAtLeast(1)) }
            }
            is CreateTaskAction.OnToggleDayOfWeek -> {
                _state.update { current ->
                    val newDays = current.selectedDaysOfWeek.toMutableSet()
                    if (newDays.contains(action.dayOfWeek)) {
                        if (newDays.size > 1) newDays.remove(action.dayOfWeek)
                    } else {
                        newDays.add(action.dayOfWeek)
                    }
                    current.copy(selectedDaysOfWeek = newDays)
                }
            }
            is CreateTaskAction.OnToggleTag -> {
                _state.update { current ->
                    val newTags = current.selectedTags.toMutableSet()
                    if (newTags.any { it.id == action.tag.id }) {
                        newTags.removeAll { it.id == action.tag.id }
                    } else {
                        newTags.add(action.tag)
                    }
                    current.copy(selectedTags = newTags)
                }
            }
            is CreateTaskAction.OnNewTagInputChange -> {
                _state.update { it.copy(newTagInput = action.input) }
            }
            CreateTaskAction.OnAddNewTag -> {
                val input = _state.value.newTagInput.trim().removePrefix("#")
                if (input.isNotBlank()) {
                    viewModelScope.launch {
                        taskRepository.insertHashTag(input).onSuccess { createdTag ->
                            _state.update { current ->
                                current.copy(
                                    newTagInput = "",
                                    selectedTags = current.selectedTags + createdTag
                                )
                            }
                        }
                    }
                }
            }
            is CreateTaskAction.OnColorSelected -> {
                _state.update { it.copy(selectedColor = action.color) }
            }
            CreateTaskAction.OnSubmit -> {
                val currentState = _state.value
                val title = currentState.title.trim()
                if (title.isBlank()) return

                viewModelScope.launch {
                    _state.update { it.copy(isSaving = true) }

                    val displayTitle = when (currentState.periodType) {
                        PeriodType.DAILY -> "每日"
                        PeriodType.INTERVAL -> "每 ${currentState.intervalDays} 天"
                        PeriodType.WEEKLY -> {
                            val dayNames = mapOf(1 to "一", 2 to "二", 3 to "三", 4 to "四", 5 to "五", 6 to "六", 7 to "日")
                            currentState.selectedDaysOfWeek.sorted().mapNotNull { dayNames[it] }.joinToString("、")
                        }
                    }

                    val period = Period(
                        id = 0,
                        type = currentState.periodType,
                        intervalDays = currentState.intervalDays,
                        daysOfWeek = currentState.selectedDaysOfWeek.sorted(),
                        displayTitle = displayTitle
                    )

                    taskRepository.createTask(
                        title = title,
                        period = period,
                        hashTags = currentState.selectedTags.toList(),
                        colorTheme = currentState.selectedColor
                    ).onSuccess {
                        _state.update { it.copy(isSaving = false) }
                        _eventChannel.send(CreateTaskEvent.TaskCreatedSuccess)
                    }.onError {
                        _state.update { it.copy(isSaving = false) }
                    }
                }
            }
            CreateTaskAction.OnDismiss -> Unit
        }
    }
}
