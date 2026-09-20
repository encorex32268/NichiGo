package com.lihan.nichigo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lihan.nichigo.core.database.dao.TaskDao
import com.lihan.nichigo.core.database.entity.HashTagEntity
import com.lihan.nichigo.core.database.entity.PeriodEntity
import com.lihan.nichigo.core.database.entity.TaskCompletionEntity
import com.lihan.nichigo.core.database.entity.TaskEntity
import com.lihan.nichigo.core.database.entity.TaskHashTagCrossRef

@Database(
    entities = [
        TaskEntity::class,
        PeriodEntity::class,
        HashTagEntity::class,
        TaskHashTagCrossRef::class,
        TaskCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
