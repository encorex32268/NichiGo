package com.lihan.nichigo.task.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.nichigo.R
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.ui.theme.AppBg
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextPrimary
import com.lihan.nichigo.ui.theme.TextSecondary
import com.lihan.nichigo.ui.theme.TextTertiary

@Composable
fun PeriodSelector(
    periodType: PeriodType,
    intervalDays: Int,
    selectedDaysOfWeek: Set<Int>,
    onPeriodTypeChange: (PeriodType) -> Unit,
    onIntervalDaysChange: (Int) -> Unit,
    onStepInterval: (Int) -> Unit,
    onToggleDayOfWeek: (Int) -> Unit,
    onSelectQuickWeekdays: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.create_task_freq_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        // Period Type Segmented Buttons
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
                val isSelected = periodType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onPeriodTypeChange(type) }
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

        // Sub-configuration based on period type
        when (periodType) {
            PeriodType.DAILY -> Unit
            PeriodType.INTERVAL -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.create_task_interval_days_fmt, intervalDays),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "2 ~ 14 天",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stepper Decrease Button
                        IconButton(
                            onClick = { onStepInterval(-1) },
                            enabled = intervalDays > 2,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (intervalDays > 2) AppBg else AppBg.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.create_task_step_decrease),
                                tint = if (intervalDays > 2) TextPrimary else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Slider
                        Slider(
                            value = intervalDays.toFloat(),
                            onValueChange = { onIntervalDaysChange(it.toInt()) },
                            valueRange = 2f..14f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryDark,
                                activeTrackColor = PrimaryDark,
                                inactiveTrackColor = BorderDivider
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Stepper Increase Button
                        IconButton(
                            onClick = { onStepInterval(1) },
                            enabled = intervalDays < 14,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (intervalDays < 14) AppBg else AppBg.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.create_task_step_increase),
                                tint = if (intervalDays < 14) TextPrimary else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.create_task_days_of_week),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )

                        // Quick Presets
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val presets = listOf(
                                stringResource(R.string.create_task_quick_weekdays) to setOf(1, 2, 3, 4, 5),
                                stringResource(R.string.create_task_quick_weekends) to setOf(6, 7),
                                stringResource(R.string.create_task_quick_all) to (1..7).toSet()
                            )

                            presets.forEach { (name, days) ->
                                val isActive = selectedDaysOfWeek == days
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isActive) PrimaryDark else AppBg)
                                        .clickable { onSelectQuickWeekdays(days) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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
                            val isSelected = selectedDaysOfWeek.contains(dayIndex)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryDark else AppBg)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryDark else BorderDivider,
                                        shape = CircleShape
                                    )
                                    .clickable { onToggleDayOfWeek(dayIndex) },
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
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PeriodSelectorIntervalPreview() {
    com.lihan.nichigo.ui.theme.NichiGoTheme {
        PeriodSelector(
            periodType = PeriodType.INTERVAL,
            intervalDays = 3,
            selectedDaysOfWeek = setOf(1, 3, 5),
            onPeriodTypeChange = {},
            onIntervalDaysChange = {},
            onStepInterval = {},
            onToggleDayOfWeek = {},
            onSelectQuickWeekdays = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PeriodSelectorWeeklyPreview() {
    com.lihan.nichigo.ui.theme.NichiGoTheme {
        PeriodSelector(
            periodType = PeriodType.WEEKLY,
            intervalDays = 2,
            selectedDaysOfWeek = setOf(1, 2, 3, 4, 5),
            onPeriodTypeChange = {},
            onIntervalDaysChange = {},
            onStepInterval = {},
            onToggleDayOfWeek = {},
            onSelectQuickWeekdays = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

