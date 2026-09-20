package com.lihan.nichigo.task.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.components.AppButton
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.core.presentation.util.ObserveAsEvents
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.presentation.components.ColorPicker
import com.lihan.nichigo.task.presentation.components.HashTagSelector
import com.lihan.nichigo.task.presentation.components.PeriodSelector
import com.lihan.nichigo.task.presentation.components.TaskCardItem
import com.lihan.nichigo.task.presentation.components.TaskTitleInput
import com.lihan.nichigo.ui.theme.AppBg
import com.lihan.nichigo.ui.theme.NichiGoTheme
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextPrimary
import com.lihan.nichigo.ui.theme.TextSecondary
import com.lihan.nichigo.ui.theme.TextTertiary
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTaskRoot(
    viewModel: CreateTaskViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            CreateTaskEvent.TaskCreatedSuccess -> onDismiss()
        }
    }

    CreateTaskScreen(
        state = state,
        onAction = { action ->
            when (action) {
                CreateTaskAction.OnDismiss -> onDismiss()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    state: CreateTaskState,
    onAction: (CreateTaskAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_task_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(CreateTaskAction.OnDismiss) }) {
                        Icon(
                            imageVector = AppIcons.Close,
                            contentDescription = stringResource(R.string.create_task_close),
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBg
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                AppButton(
                    onClick = {
                        focusManager.clearFocus()
                        onAction(CreateTaskAction.OnSubmit)
                    },
                    enabled = true,
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.create_task_submit),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Task Title Input (Modular Component)
            TaskTitleInput(
                title = state.title,
                onTitleChange = { onAction(CreateTaskAction.OnTitleChange(it)) },
                titleError = state.titleError
            )

            // 2. Frequency Selector (Modular Component)
            PeriodSelector(
                periodType = state.periodType,
                intervalDays = state.intervalDays,
                selectedDaysOfWeek = state.selectedDaysOfWeek,
                onPeriodTypeChange = { onAction(CreateTaskAction.OnPeriodTypeChange(it)) },
                onIntervalDaysChange = { onAction(CreateTaskAction.OnIntervalDaysChange(it)) },
                onStepInterval = { onAction(CreateTaskAction.OnStepInterval(it)) },
                onToggleDayOfWeek = { onAction(CreateTaskAction.OnToggleDayOfWeek(it)) },
                onSelectQuickWeekdays = { onAction(CreateTaskAction.OnSelectQuickWeekdays(it)) }
            )

            // 3. Category HashTags (Modular Component)
            HashTagSelector(
                availableTags = state.availableTags,
                selectedTags = state.selectedTags,
                newTagInput = state.newTagInput,
                onToggleTag = { onAction(CreateTaskAction.OnToggleTag(it)) },
                onNewTagInputChange = { onAction(CreateTaskAction.OnNewTagInputChange(it)) },
                onAddNewTag = { onAction(CreateTaskAction.OnAddNewTag) }
            )

            // 4. Color Theme Selector (Modular Component)
            ColorPicker(
                selectedColor = state.selectedColor,
                availableColors = state.availableColors,
                onColorSelected = { onAction(CreateTaskAction.OnColorSelected(it)) }
            )

            // 5. Real-time Live TaskCard Preview
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.create_task_preview_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                val previewPeriodTitle = when (state.periodType) {
                    PeriodType.DAILY -> stringResource(R.string.create_task_freq_daily)
                    PeriodType.INTERVAL -> stringResource(R.string.create_task_interval_days_fmt, state.intervalDays)
                    PeriodType.WEEKLY -> {
                        val sorted = state.selectedDaysOfWeek.sorted()
                        when {
                            sorted.size == 7 -> stringResource(R.string.create_task_freq_daily)
                            sorted == listOf(1, 2, 3, 4, 5) -> stringResource(R.string.create_task_quick_weekdays)
                            sorted == listOf(6, 7) -> stringResource(R.string.create_task_quick_weekends)
                            else -> {
                                val dayNames = mapOf(
                                    1 to stringResource(R.string.day_mon),
                                    2 to stringResource(R.string.day_tue),
                                    3 to stringResource(R.string.day_wed),
                                    4 to stringResource(R.string.day_thu),
                                    5 to stringResource(R.string.day_fri),
                                    6 to stringResource(R.string.day_sat),
                                    7 to stringResource(R.string.day_sun)
                                )
                                sorted.mapNotNull { dayNames[it] }.joinToString("、")
                            }
                        }
                    }
                }

                val previewTitle = state.title.ifBlank {
                    stringResource(R.string.create_task_preview_placeholder)
                }

                val previewTask = TaskCard(
                    id = 0L,
                    title = previewTitle,
                    isCompleted = false,
                    createdAt = System.currentTimeMillis(),
                    colorTheme = state.selectedColor,
                    period = Period(
                        id = 0L,
                        type = state.periodType,
                        intervalDays = state.intervalDays,
                        daysOfWeek = state.selectedDaysOfWeek.sorted(),
                        displayTitle = previewPeriodTitle
                    ),
                    hashTags = state.selectedTags.toList()
                )

                TaskCardItem(
                    task = previewTask,
                    onToggle = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskScreenPreview() {
    NichiGoTheme {
        CreateTaskScreen(
            state = CreateTaskState(
                availableTags = listOf(
                    HashTag(1, "語言學習", 0xFF2563EB),
                    HashTag(2, "體能鍛鍊", 0xFFD97706)
                )
            ),
            onAction = {}
        )
    }
}
