package com.lihan.nichigo.task.presentation.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.presentation.components.CircularProgressRing
import com.lihan.nichigo.task.presentation.components.PillBadge
import com.lihan.nichigo.task.presentation.components.TaskCardItem
import com.lihan.nichigo.ui.theme.AppBg
import com.lihan.nichigo.ui.theme.NichiGoTheme
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.ProgressBlue
import com.lihan.nichigo.ui.theme.TextPrimary
import com.lihan.nichigo.ui.theme.TextSecondary
import com.lihan.nichigo.ui.theme.TextTertiary
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskRoot(
    viewModel: TaskViewModel = koinViewModel(),
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        state = state,
        onAction = { action ->
            when (action) {
                TaskAction.OnCreateTaskClick -> onNavigateToCreate()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        modifier = modifier
    )
}

@Composable
fun TaskScreen(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TaskAction.OnCreateTaskClick) },
                shape = CircleShape,
                containerColor = PrimaryDark,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = AppIcons.Add,
                    contentDescription = stringResource(R.string.create_task_fab)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Daily Progress Overview Card
            item(key = "progress_header") {
                DailyProgressHeader(
                    progress = state.progress,
                    completedCount = state.completedCount,
                    totalCount = state.totalCount
                )
            }

            // Empty state or Task items
            if (state.tasks.isEmpty() && !state.isLoading) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.home_empty_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.home_empty_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = state.tasks,
                    key = { it.id }
                ) { task ->
                    TaskCardItem(
                        task = task,
                        onToggle = { onAction(TaskAction.OnToggleTask(task.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyProgressHeader(
    progress: Float,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressRing(
            progress = progress,
            trackColor = PrimaryDark.copy(alpha = 0.1f),
            progressColor = ProgressBlue
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                R.string.home_completed_count_fmt,
                completedCount,
                totalCount
            ),
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskScreenPreview() {
    NichiGoTheme {
        TaskScreen(
            state = TaskState(
                tasks = listOf(
                    TaskCard(
                        id = 1,
                        title = "記 100 個日文單詞",
                        isCompleted = true,
                        colorTheme = 0xFFE6F8F3,
                        period = Period(1, PeriodType.DAILY, 1, emptyList(), "每日"),
                        hashTags = listOf(HashTag(1, "語言學習", 0xFF2563EB))
                    ),
                    TaskCard(
                        id = 2,
                        title = "深蹲與核心訓練 20 分鐘",
                        isCompleted = false,
                        colorTheme = 0xFFEFF4FF,
                        period = Period(2, PeriodType.INTERVAL, 2, emptyList(), "每 2 天"),
                        hashTags = listOf(HashTag(2, "體能鍛鍊", 0xFFD97706))
                    )
                ).sortedBy { it.isCompleted },
                totalCount = 2,
                completedCount = 1,
                progress = 0.5f
            ),
            onAction = {}
        )
    }
}
