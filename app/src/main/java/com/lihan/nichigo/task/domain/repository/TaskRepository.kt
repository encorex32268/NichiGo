package com.lihan.nichigo.task.domain.repository

import com.lihan.nichigo.core.domain.util.DataError
import com.lihan.nichigo.core.domain.util.EmptyResult
import com.lihan.nichigo.core.domain.util.Result
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.TaskCard
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface TaskRepository {
    fun getTasksForDate(date: LocalDate): Flow<List<TaskCard>>
    fun getMonthlyCompletionStatus(yearMonth: YearMonth): Flow<Map<LocalDate, DayCompletionStatus>>
    suspend fun toggleTaskCompletion(taskId: Long, date: LocalDate): EmptyResult<DataError.Local>
    fun getAllHashTags(): Flow<List<HashTag>>
    suspend fun insertHashTag(title: String, color: Long = 0xFF71717A): Result<HashTag, DataError.Local>
    suspend fun createTask(
        title: String,
        period: Period,
        hashTags: List<HashTag>,
        colorTheme: Long
    ): Result<Long, DataError.Local>
}
