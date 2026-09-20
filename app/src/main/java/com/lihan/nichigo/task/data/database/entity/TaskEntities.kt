package com.lihan.nichigo.task.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val intervalDays: Int = 1,
    val daysOfWeekCsv: String = "",
    val displayTitle: String
)

@Entity(
    tableName = "hashtags",
    indices = [Index(value = ["title"], unique = true)]
)
data class HashTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val color: Long = 0xFF71717A
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = PeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["periodId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("periodId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val colorTheme: Long = 0xFFF3E8FF,
    val periodId: Long
)

@Entity(
    tableName = "task_hashtag_cross_ref",
    primaryKeys = ["taskId", "hashTagId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HashTagEntity::class,
            parentColumns = ["id"],
            childColumns = ["hashTagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("hashTagId")]
)
data class TaskHashTagCrossRef(
    val taskId: Long,
    val hashTagId: Long
)

@Entity(
    tableName = "task_completions",
    primaryKeys = ["taskId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("epochDay")]
)
data class TaskCompletionEntity(
    val taskId: Long,
    val epochDay: Long
)

/**
 * Composite entity linking Task, its associated Period, and its HashTags via Room Relations.
 */
data class TaskWithDetails(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "periodId",
        entityColumn = "id"
    )
    val period: PeriodEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskHashTagCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "hashTagId"
        )
    )
    val hashTags: List<HashTagEntity>
)
