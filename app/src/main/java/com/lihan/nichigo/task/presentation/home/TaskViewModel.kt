package com.lihan.nichigo.task.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    val state: StateFlow<TaskState> = taskRepository.getTasksForDate(LocalDate.now())
        .map { tasks ->
            val sortedTasks = tasks.sortedBy { it.isCompleted }
            val total = sortedTasks.size
            val completed = sortedTasks.count { it.isCompleted }
            val progress = if (total > 0) completed.toFloat() / total else 0f
            TaskState(
                tasks = sortedTasks,
                totalCount = total,
                completedCount = completed,
                progress = progress,
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
            initialValue = TaskState(isLoading = true)
        )

    fun onAction(action: TaskAction) {
        when (action) {
            is TaskAction.OnToggleTask -> {
                viewModelScope.launch {
                    taskRepository.toggleTaskCompletion(action.taskId, LocalDate.now())
                }
            }
            TaskAction.OnCreateTaskClick -> Unit
        }
    }
}
