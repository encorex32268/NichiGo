package com.lihan.nichigo.task.presentation.home

sealed interface TaskAction {
    data class OnToggleTask(val taskId: Long) : TaskAction
    data object OnCreateTaskClick : TaskAction
}
