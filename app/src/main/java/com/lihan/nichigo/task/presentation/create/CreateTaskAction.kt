package com.lihan.nichigo.task.presentation.create

import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.PeriodType

sealed interface CreateTaskAction {
    data class OnTitleChange(val title: String) : CreateTaskAction
    data class OnPeriodTypeChange(val type: PeriodType) : CreateTaskAction
    data class OnIntervalDaysChange(val days: Int) : CreateTaskAction
    data class OnToggleDayOfWeek(val dayOfWeek: Int) : CreateTaskAction
    data class OnToggleTag(val tag: HashTag) : CreateTaskAction
    data class OnNewTagInputChange(val input: String) : CreateTaskAction
    data object OnAddNewTag : CreateTaskAction
    data class OnColorSelected(val color: Long) : CreateTaskAction
    data object OnSubmit : CreateTaskAction
    data object OnDismiss : CreateTaskAction
}
