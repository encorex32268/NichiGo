package com.lihan.nichigo.task.data.repository

import com.lihan.nichigo.core.domain.util.DataError
import com.lihan.nichigo.core.domain.util.EmptyResult
import com.lihan.nichigo.core.domain.util.Result
import com.lihan.nichigo.core.database.dao.TaskDao
import com.lihan.nichigo.core.database.entity.HashTagEntity
import com.lihan.nichigo.core.database.entity.TaskEntity
import com.lihan.nichigo.core.database.entity.TaskWithDetails
import com.lihan.nichigo.task.data.mapper.toDomain
import com.lihan.nichigo.task.data.mapper.toEntity
import com.lihan.nichigo.task.data.mapper.toTaskCard
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasksForDate(date: LocalDate): Flow<List<TaskCard>> {
        val epochDay = date.toEpochDay()
        return combine(
            taskDao.getAllTasksWithDetails(),
            taskDao.getCompletedTaskIdsOnDate(epochDay)
        ) { allTasks, completedIds ->
            val completedSet = completedIds.toSet()
            allTasks
                .filter { isTaskScheduledOnDate(it, date) }
                .map { taskWithDetails ->
                    val isCompleted = completedSet.contains(taskWithDetails.task.id)
                    taskWithDetails.toTaskCard(isCompletedOnDate = isCompleted)
                }
                .sortedWith(
                    compareBy<TaskCard> { it.isCompleted }
                        .thenBy { it.id }
                )
        }
    }

    override fun getMonthlyCompletionStatus(yearMonth: YearMonth): Flow<Map<LocalDate, DayCompletionStatus>> {
        val startEpochDay = yearMonth.atDay(1).toEpochDay()
        val endEpochDay = yearMonth.atEndOfMonth().toEpochDay()

        return combine(
            taskDao.getAllTasksWithDetails(),
            taskDao.getCompletionsInRange(startEpochDay, endEpochDay)
        ) { allTasks, completions ->
            val completionMap = completions.groupBy({ it.epochDay }, { it.taskId })
            val result = mutableMapOf<LocalDate, DayCompletionStatus>()

            for (day in 1..yearMonth.lengthOfMonth()) {
                val date = yearMonth.atDay(day)
                val epoch = date.toEpochDay()
                val scheduledTasks = allTasks.filter { isTaskScheduledOnDate(it, date) }

                if (scheduledTasks.isEmpty()) {
                    result[date] = DayCompletionStatus.NONE
                } else {
                    val completedForDay = completionMap[epoch]?.toSet() ?: emptySet()
                    val completedCount = scheduledTasks.count { completedForDay.contains(it.task.id) }
                    when {
                        completedCount == scheduledTasks.size -> result[date] = DayCompletionStatus.ALL_COMPLETED
                        completedCount > 0 -> result[date] = DayCompletionStatus.PARTIAL
                        else -> result[date] = DayCompletionStatus.NOT_COMPLETED
                    }
                }
            }
            result
        }
    }

    override suspend fun toggleTaskCompletion(taskId: Long, date: LocalDate): EmptyResult<DataError.Local> {
        return try {
            taskDao.toggleCompletion(taskId, date.toEpochDay())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getAllHashTags(): Flow<List<HashTag>> {
        return taskDao.getAllHashTags().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertHashTag(title: String, color: Long): Result<HashTag, DataError.Local> {
        return try {
            val existing = taskDao.getHashTagByTitle(title)
            if (existing != null) {
                Result.Success(existing.toDomain())
            } else {
                val newId = taskDao.insertHashTag(HashTagEntity(title = title, color = color))
                Result.Success(HashTag(id = newId, title = title, color = color))
            }
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun createTask(
        title: String,
        period: Period,
        hashTags: List<HashTag>,
        colorTheme: Long
    ): Result<Long, DataError.Local> {
        return try {
            val periodEntity = period.toEntity()
            val periodId = taskDao.insertPeriod(periodEntity)

            val tagIds = hashTags.map { tag ->
                val existing = taskDao.getHashTagByTitle(tag.title)
                existing?.id ?: taskDao.insertHashTag(tag.toEntity())
            }

            val taskEntity = TaskEntity(
                title = title,
                createdAt = System.currentTimeMillis(),
                colorTheme = colorTheme,
                periodId = periodId
            )

            val taskId = taskDao.insertTaskWithDetails(taskEntity, tagIds)
            Result.Success(taskId)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    companion object {
        fun isTaskScheduledOnDate(taskWithDetails: TaskWithDetails, date: LocalDate): Boolean {
            val period = taskWithDetails.period?.toDomain() ?: return true
            return when (period.type) {
                PeriodType.DAILY -> true
                PeriodType.WEEKLY -> {
                    if (period.daysOfWeek.isEmpty()) true
                    else date.dayOfWeek.value in period.daysOfWeek
                }
                PeriodType.INTERVAL -> {
                    val interval = if (period.intervalDays <= 0) 1 else period.intervalDays
                    val createdDate = LocalDate.ofEpochDay(taskWithDetails.task.createdAt / (1000 * 60 * 60 * 24))
                    val diff = date.toEpochDay() - createdDate.toEpochDay()
                    diff >= 0 && (diff % interval == 0L)
                }
            }
        }
    }
}
