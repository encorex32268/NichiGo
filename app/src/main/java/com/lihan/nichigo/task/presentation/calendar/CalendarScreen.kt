package com.lihan.nichigo.task.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.presentation.components.TaskCardItem
import com.lihan.nichigo.ui.theme.NichiGoTheme
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarRoot(
    viewModel: CalendarViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalendarScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun CalendarScreen(
    state: CalendarState,
    onAction: (CalendarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar Card Container
            item(key = "calendar_card") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        // Month Header Navigation
                        CalendarMonthHeader(
                            yearMonth = state.selectedYearMonth,
                            onPrevious = { onAction(CalendarAction.OnPreviousMonth) },
                            onNext = { onAction(CalendarAction.OnNextMonth) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weekday labels
                        CalendarWeekdayHeader()

                        Spacer(modifier = Modifier.height(10.dp))

                        // Days Grid
                        CalendarGrid(
                            yearMonth = state.selectedYearMonth,
                            selectedDate = state.selectedDate,
                            completionStatus = state.monthlyCompletionStatus,
                            onDateClick = { date -> onAction(CalendarAction.OnSelectDate(date)) }
                        )
                    }
                }
            }

            // Selected Date Tasks Section Header
            item(key = "selected_date_header") {
                val formatter = DateTimeFormatter.ofPattern("M月d日 (E)", Locale.TAIWAN)
                val dateStr = state.selectedDate.format(formatter)
                Text(
                    text = stringResource(R.string.calendar_selected_date_fmt, dateStr),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Tasks List on Selected Date
            if (state.tasksOnSelectedDate.isEmpty()) {
                item(key = "empty_day") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.calendar_empty_day),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            } else {
                items(
                    items = state.tasksOnSelectedDate,
                    key = { it.id }
                ) { task ->
                    TaskCardItem(
                        task = task,
                        onToggle = { onAction(CalendarAction.OnToggleTask(task.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMonthHeader(
    yearMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy年 M月", Locale.TAIWAN)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = yearMonth.format(formatter),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Row {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = AppIcons.ArrowLeft,
                    contentDescription = stringResource(R.string.calendar_prev_month),
                    tint = Color(0xFF334155)
                )
            }
            IconButton(
                onClick = onNext,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = AppIcons.ArrowRight,
                    contentDescription = stringResource(R.string.calendar_next_month),
                    tint = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
private fun CalendarWeekdayHeader(modifier: Modifier = Modifier) {
    val days = listOf(
        stringResource(R.string.day_sun),
        stringResource(R.string.day_mon),
        stringResource(R.string.day_tue),
        stringResource(R.string.day_wed),
        stringResource(R.string.day_thu),
        stringResource(R.string.day_fri),
        stringResource(R.string.day_sat)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        days.forEach { dayName ->
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    completionStatus: Map<LocalDate, DayCompletionStatus>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    // Sunday is 0 offset in this header
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val leadingEmptyDays = if (firstDayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfWeek.value

    val totalCells = leadingEmptyDays + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayOfMonth = cellIndex - leadingEmptyDays + 1

                    if (dayOfMonth in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayOfMonth)
                        val isSelected = date == selectedDate
                        val isToday = date == LocalDate.now()
                        val status = completionStatus[date] ?: DayCompletionStatus.NONE

                        DayCell(
                            day = dayOfMonth,
                            isSelected = isSelected,
                            isToday = isToday,
                            status = status,
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    status: DayCompletionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> Color(0xFF18181B)
                        isToday -> Color(0xFFF1F5F9)
                        else -> Color.Transparent
                    }
                )
                .border(
                    width = if (isToday && !isSelected) 1.dp else 0.dp,
                    color = if (isToday && !isSelected) Color(0xFFCBD5E1) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected -> Color.White
                    isToday -> Color(0xFF0F172A)
                    else -> Color(0xFF334155)
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Status Indicator Dot
        val dotColor = when (status) {
            DayCompletionStatus.ALL_COMPLETED -> Color(0xFF059669)
            DayCompletionStatus.PARTIAL -> Color(0xFF2563EB)
            DayCompletionStatus.NOT_COMPLETED -> Color(0xFFCBD5E1)
            DayCompletionStatus.NONE -> Color.Transparent
        }

        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    NichiGoTheme {
        CalendarScreen(
            state = CalendarState(
                selectedYearMonth = YearMonth.now(),
                selectedDate = LocalDate.now(),
                monthlyCompletionStatus = mapOf(
                    LocalDate.now() to DayCompletionStatus.ALL_COMPLETED
                ),
                tasksOnSelectedDate = listOf(
                    TaskCard(
                        id = 1,
                        title = "記 100 個日文單詞",
                        isCompleted = true,
                        colorTheme = 0xFFE6F8F3,
                        period = Period(1, PeriodType.DAILY, 1, emptyList(), "每日"),
                        hashTags = listOf(HashTag(1, "語言學習", 0xFF2563EB))
                    )
                )
            ),
            onAction = {}
        )
    }
}
