package com.lihan.nichigo.task.presentation.home

import androidx.compose.runtime.Immutable
import com.lihan.nichigo.task.domain.model.TaskCard

@Immutable
data class TaskState(
    val tasks: List<TaskCard> = emptyList(),
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val progress: Float = 0f,
    val isLoading: Boolean = false
)
