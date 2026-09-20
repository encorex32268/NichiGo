package com.lihan.nichigo.task.presentation.create

import androidx.compose.runtime.Immutable
import com.lihan.nichigo.core.presentation.util.UiText
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.ui.theme.MacaronPalette

@Immutable
data class CreateTaskState(
    val title: String = "",
    val periodType: PeriodType = PeriodType.DAILY,
    val intervalDays: Int = 2,
    val selectedDaysOfWeek: Set<Int> = setOf(1, 3, 5),
    val availableTags: List<HashTag> = emptyList(),
    val selectedTags: Set<HashTag> = emptySet(),
    val newTagInput: String = "",
    val selectedColor: Long = MacaronPalette.first(),
    val availableColors: List<Long> = MacaronPalette,
    val isSaving: Boolean = false,
    val error: UiText? = null
)
