package com.lihan.nichigo.task.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import com.lihan.nichigo.task.presentation.components.TaskCardItem
import com.lihan.nichigo.ui.theme.AppBg
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.CheckboxBorder
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateTaskScreen(
    state: CreateTaskState,
    onAction: (CreateTaskAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                AppButton(
                    onClick = { onAction(CreateTaskAction.OnSubmit) },
                    enabled = state.title.isNotBlank(),
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Task Title Input (Enlarged box with 30 char limit & counter)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.create_task_name_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onAction(CreateTaskAction.OnTitleChange(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.create_task_name_placeholder),
                            color = TextTertiary
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    minLines = 2,
                    maxLines = 3,
                    supportingText = {
                        Text(
                            text = stringResource(R.string.create_task_char_counter, state.title.length),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryDark,
                        unfocusedBorderColor = BorderDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Frequency Selector
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.create_task_freq_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                // Period Type Segments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BorderDivider.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val types = listOf(
                        PeriodType.DAILY to stringResource(R.string.create_task_freq_daily),
                        PeriodType.INTERVAL to stringResource(R.string.create_task_freq_interval),
                        PeriodType.WEEKLY to stringResource(R.string.create_task_freq_weekly)
                    )

                    types.forEach { (type, label) ->
                        val isSelected = state.periodType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onAction(CreateTaskAction.OnPeriodTypeChange(type)) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                // Sub-selectors based on period type
                when (state.periodType) {
                    PeriodType.DAILY -> Unit
                    PeriodType.INTERVAL -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.create_task_interval_days_fmt, state.intervalDays),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Slider(
                                value = state.intervalDays.toFloat(),
                                onValueChange = { onAction(CreateTaskAction.OnIntervalDaysChange(it.toInt())) },
                                valueRange = 2f..14f,
                                steps = 12
                            )
                        }
                    }
                    PeriodType.WEEKLY -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.create_task_days_of_week),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val weekdays = listOf(
                                    1 to stringResource(R.string.day_mon),
                                    2 to stringResource(R.string.day_tue),
                                    3 to stringResource(R.string.day_wed),
                                    4 to stringResource(R.string.day_thu),
                                    5 to stringResource(R.string.day_fri),
                                    6 to stringResource(R.string.day_sat),
                                    7 to stringResource(R.string.day_sun)
                                )

                                weekdays.forEach { (dayIndex, name) ->
                                    val isSelected = state.selectedDaysOfWeek.contains(dayIndex)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) PrimaryDark else AppBg
                                            )
                                            .clickable {
                                                onAction(CreateTaskAction.OnToggleDayOfWeek(dayIndex))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Category HashTags (Searchable with keyword filtering & dynamic add)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.create_task_hashtag_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                // Currently selected tags (if any)
                if (state.selectedTags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.selectedTags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryDark)
                                    .border(
                                        width = 1.dp,
                                        color = PrimaryDark,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onAction(CreateTaskAction.OnToggleTag(tag)) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "#${tag.title}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = AppIcons.Close,
                                        contentDescription = stringResource(R.string.common_clear),
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                val cleanInput = state.newTagInput.trim().removePrefix("#")
                val matchingTags = remember(state.availableTags, cleanInput) {
                    if (cleanInput.isBlank()) {
                        emptyList()
                    } else {
                        state.availableTags.filter { it.title.contains(cleanInput, ignoreCase = true) }
                    }
                }
                val hasExactMatch = remember(state.availableTags, cleanInput) {
                    state.availableTags.any { it.title.equals(cleanInput, ignoreCase = true) }
                }

                // Search / Add Tag Input Field
                OutlinedTextField(
                    value = state.newTagInput,
                    onValueChange = { onAction(CreateTaskAction.OnNewTagInputChange(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.create_task_search_tag_placeholder),
                            color = TextTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (cleanInput.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onAction(CreateTaskAction.OnNewTagInputChange("")) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = AppIcons.Close,
                                        contentDescription = stringResource(R.string.common_clear),
                                        tint = TextTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!hasExactMatch && cleanInput.isNotBlank()) {
                                onAction(CreateTaskAction.OnAddNewTag)
                            }
                            focusManager.clearFocus()
                        }
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryDark,
                        unfocusedBorderColor = BorderDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tags display (matching tags only shown when cleanInput.isNotBlank(), maxLines = 3)
                if (cleanInput.isNotBlank() && (matchingTags.isNotEmpty() || !hasExactMatch)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxLines = 3
                    ) {
                        matchingTags.forEach { tag ->
                            val isSelected = state.selectedTags.any { it.id == tag.id }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) PrimaryDark else Color.White
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryDark else BorderDivider,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onAction(CreateTaskAction.OnToggleTag(tag))
                                        onAction(CreateTaskAction.OnNewTagInputChange(""))
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "#${tag.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }

                        // If input has text and not already an existing tag, show "+ 新增「#...」" chip
                        if (!hasExactMatch) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = BorderDivider,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onAction(CreateTaskAction.OnAddNewTag)
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.create_task_add_tag_fmt, cleanInput),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDark
                                )
                            }
                        }
                    }
                }
            }

            // 4. Color Theme Selector
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.create_task_color_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.availableColors.forEach { colorLong ->
                        val isSelected = state.selectedColor == colorLong
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) PrimaryDark else CheckboxBorder,
                                    shape = CircleShape
                                )
                                .clickable { onAction(CreateTaskAction.OnColorSelected(colorLong)) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = AppIcons.Check,
                                    contentDescription = null,
                                    tint = PrimaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Real-time Live TaskCard Preview (Positioned at bottom)
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
                        val dayNames = mapOf(
                            1 to stringResource(R.string.day_mon),
                            2 to stringResource(R.string.day_tue),
                            3 to stringResource(R.string.day_wed),
                            4 to stringResource(R.string.day_thu),
                            5 to stringResource(R.string.day_fri),
                            6 to stringResource(R.string.day_sat),
                            7 to stringResource(R.string.day_sun)
                        )
                        state.selectedDaysOfWeek.sorted().mapNotNull { dayNames[it] }.joinToString("、")
                    }
                }

                val previewTask = TaskCard(
                    id = 0L,
                    title = state.title,
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
