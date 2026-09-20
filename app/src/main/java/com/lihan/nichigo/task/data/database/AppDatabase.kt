package com.lihan.nichigo.task.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lihan.nichigo.task.data.database.dao.TaskDao
import com.lihan.nichigo.task.data.database.entity.HashTagEntity
import com.lihan.nichigo.task.data.database.entity.PeriodEntity
import com.lihan.nichigo.task.data.database.entity.TaskCompletionEntity
import com.lihan.nichigo.task.data.database.entity.TaskEntity
import com.lihan.nichigo.task.data.database.entity.TaskHashTagCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseFactory.create(context).also { INSTANCE = it }
            }
        }
    }
}

object DatabaseFactory {
    fun create(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "nichigo_task.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateSeedData(AppDatabase.getInstance(context))
                }
            }
        }).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    private suspend fun prepopulateSeedData(db: AppDatabase) {
        val dao = db.taskDao()

        // 1. Periods
        val dailyPeriodId = dao.insertPeriod(
            PeriodEntity(type = "DAILY", intervalDays = 1, displayTitle = "每日")
        )
        val intervalPeriodId = dao.insertPeriod(
            PeriodEntity(type = "INTERVAL", intervalDays = 2, displayTitle = "每 2 天")
        )
        val weeklyPeriodId = dao.insertPeriod(
            PeriodEntity(type = "WEEKLY", daysOfWeekCsv = "1,3,5", displayTitle = "一、三、五")
        )

        // 2. HashTags
        val tag1 = dao.insertHashTag(HashTagEntity(title = "語言學習", color = 0xFF2563EB))
        val tag2 = dao.insertHashTag(HashTagEntity(title = "核心詞彙", color = 0xFF059669))
        val tag3 = dao.insertHashTag(HashTagEntity(title = "體能鍛鍊", color = 0xFFD97706))
        val tag4 = dao.insertHashTag(HashTagEntity(title = "居家徒手", color = 0xFF7C3AED))
        val tag5 = dao.insertHashTag(HashTagEntity(title = "生活風格", color = 0xFF2563EB))
        val tag6 = dao.insertHashTag(HashTagEntity(title = "靜心閱讀", color = 0xFF9333EA))
        val tag7 = dao.insertHashTag(HashTagEntity(title = "心智冥想", color = 0xFF0D9488))

        val now = System.currentTimeMillis()
        val todayEpochDay = LocalDate.now().toEpochDay()

        // 3. Demo Tasks matching Stitch mockups
        val task1Id = dao.insertTask(
            TaskEntity(
                title = "記 100 個日文單詞",
                createdAt = now,
                colorTheme = 0xFFE6F8F3,
                periodId = dailyPeriodId
            )
        )
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task1Id, tag1))
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task1Id, tag2))
        dao.insertCompletion(TaskCompletionEntity(task1Id, todayEpochDay))

        val task2Id = dao.insertTask(
            TaskEntity(
                title = "深蹲與核心訓練 20 分鐘",
                createdAt = now,
                colorTheme = 0xFFEFF4FF,
                periodId = intervalPeriodId
            )
        )
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task2Id, tag3))
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task2Id, tag4))
        dao.insertCompletion(TaskCompletionEntity(task2Id, todayEpochDay))

        val task3Id = dao.insertTask(
            TaskEntity(
                title = "晨間手沖咖啡與閱讀",
                createdAt = now,
                colorTheme = 0xFFFEF3C7,
                periodId = weeklyPeriodId
            )
        )
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task3Id, tag5))
        dao.insertTaskHashTagCrossRef(TaskHashTagCrossRef(task3Id, tag6))
    }
}
