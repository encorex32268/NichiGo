package com.lihan.nichigo.task.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.BrandEmerald
import com.lihan.nichigo.ui.theme.CheckboxBorder
import com.lihan.nichigo.ui.theme.NichiGoTheme
import com.lihan.nichigo.ui.theme.TagGray
import com.lihan.nichigo.ui.theme.TextPrimary
import com.lihan.nichigo.ui.theme.periodPillColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCardItem(
    task: TaskCard,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.55f else 1f,
        label = "TaskAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha },
        shape = RoundedCornerShape(20.dp),
        color = if (task.colorTheme != 0L) Color(task.colorTheme) else Color.White,
        onClick = onToggle,
        border = BorderStroke(
            width = 1.dp,
            color = if (task.isCompleted) BorderDivider else Color.Transparent,
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox Circle
            val checkmarkDesc = stringResource(R.string.task_toggle_complete)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) BrandEmerald else Color.White
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (task.isCompleted) BrandEmerald else CheckboxBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = checkmarkDesc,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Middle & Right Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                }
                Spacer(modifier = Modifier.height(8.dp))
                // Period Pill with dedicated color based on interval
                val (pillBg, pillText) = periodPillColors(task.period.type)
                PillBadge(
                    text = task.period.displayTitle,
                    containerColor = pillBg,
                    contentColor = pillText,
                    isFullRounded = true
                )

                // Hashtags: No background container, just #hashtag in gray text
                if (task.hashTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        task.hashTags.forEach { tag ->
                            Text(
                                text = "#${tag.title}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TagGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCardItemPreview() {
    NichiGoTheme {
        TaskCardItem(
            task = TaskCard(
                id = 1,
                title = "記 100 個日文單詞",
                isCompleted = false,
                colorTheme = 0xFFE6F8F3,
                period = Period(1, PeriodType.DAILY, 1, emptyList(), "每日"),
                hashTags = listOf(
                    HashTag(1, "語言學習", 0xFF2563EB),
                    HashTag(2, "核心詞彙", 0xFF059669)
                )
            ),
            onToggle = {}
        )
    }
}
