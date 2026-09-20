package com.lihan.nichigo.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lihan.nichigo.core.database.entity.HashTagEntity
import com.lihan.nichigo.core.database.entity.PeriodEntity
import com.lihan.nichigo.core.database.entity.TaskCompletionEntity
import com.lihan.nichigo.core.database.entity.TaskEntity
import com.lihan.nichigo.core.database.entity.TaskHashTagCrossRef
import com.lihan.nichigo.core.database.entity.TaskWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasksWithDetails(): Flow<List<TaskWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHashTag(hashTag: HashTagEntity): Long

    @Query("SELECT * FROM hashtags WHERE title = :title LIMIT 1")
    suspend fun getHashTagByTitle(title: String): HashTagEntity?

    @Query("SELECT * FROM hashtags ORDER BY id ASC")
    fun getAllHashTags(): Flow<List<HashTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskHashTagCrossRef(crossRef: TaskHashTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: TaskCompletionEntity)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND epochDay = :epochDay")
    suspend fun deleteCompletion(taskId: Long, epochDay: Long)

    @Query("SELECT COUNT(*) FROM task_completions WHERE taskId = :taskId AND epochDay = :epochDay")
    suspend fun isCompleted(taskId: Long, epochDay: Long): Int

    @Query("SELECT taskId FROM task_completions WHERE epochDay = :epochDay")
    fun getCompletedTaskIdsOnDate(epochDay: Long): Flow<List<Long>>

    @Query("SELECT * FROM task_completions WHERE epochDay BETWEEN :startEpochDay AND :endEpochDay")
    fun getCompletionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<TaskCompletionEntity>>

    @Transaction
    suspend fun toggleCompletion(taskId: Long, epochDay: Long) {
        val count = isCompleted(taskId, epochDay)
        if (count > 0) {
            deleteCompletion(taskId, epochDay)
        } else {
            insertCompletion(TaskCompletionEntity(taskId = taskId, epochDay = epochDay))
        }
    }

    @Transaction
    suspend fun insertTaskWithDetails(task: TaskEntity, hashTagIds: List<Long>): Long {
        val taskId = insertTask(task)
        for (tagId in hashTagIds) {
            insertTaskHashTagCrossRef(TaskHashTagCrossRef(taskId = taskId, hashTagId = tagId))
        }
        return taskId
    }

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int
}
