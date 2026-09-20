package com.lihan.nichigo

import com.lihan.nichigo.core.database.entity.HashTagEntity
import com.lihan.nichigo.core.database.entity.PeriodEntity
import com.lihan.nichigo.core.database.entity.TaskEntity
import com.lihan.nichigo.core.database.entity.TaskWithDetails
import com.lihan.nichigo.task.data.mapper.toDomain
import com.lihan.nichigo.task.data.mapper.toEntity
import com.lihan.nichigo.task.data.mapper.toTaskCard
import com.lihan.nichigo.task.data.repository.TaskRepositoryImpl
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TaskRepositoryTest {

    @Test
    fun testPeriodEntityConversion() {
        val domainPeriod = Period(
            id = 10L,
            type = PeriodType.WEEKLY,
            intervalDays = 1,
            daysOfWeek = listOf(1, 3, 5),
            displayTitle = "一、三、五"
        )
        val entity = domainPeriod.toEntity()
        assertEquals("WEEKLY", entity.type)
        assertEquals("1,3,5", entity.daysOfWeekCsv)

        val restored = entity.toDomain()
        assertEquals(domainPeriod.type, restored.type)
        assertEquals(domainPeriod.daysOfWeek, restored.daysOfWeek)
        assertEquals(domainPeriod.displayTitle, restored.displayTitle)
    }

    @Test
    fun testDailyTaskScheduledEveryDay() {
        val dailyTask = TaskWithDetails(
            task = TaskEntity(id = 1L, title = "每日讀書", periodId = 1L),
            period = PeriodEntity(id = 1L, type = "DAILY", displayTitle = "每日"),
            hashTags = emptyList()
        )

        val monday = LocalDate.of(2026, 9, 21) // Monday
        val sunday = LocalDate.of(2026, 9, 27) // Sunday

        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(dailyTask, monday))
        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(dailyTask, sunday))
    }

    @Test
    fun testWeeklyTaskScheduledOnlyOnSpecifiedDays() {
        val weeklyTask = TaskWithDetails(
            task = TaskEntity(id = 2L, title = "健身訓練", periodId = 2L),
            period = PeriodEntity(id = 2L, type = "WEEKLY", daysOfWeekCsv = "1,3,5", displayTitle = "一、三、五"),
            hashTags = emptyList()
        )

        val monday = LocalDate.of(2026, 9, 21)    // Monday (1)
        val tuesday = LocalDate.of(2026, 9, 22)   // Tuesday (2)
        val wednesday = LocalDate.of(2026, 9, 23) // Wednesday (3)
        val thursday = LocalDate.of(2026, 9, 24)  // Thursday (4)
        val friday = LocalDate.of(2026, 9, 25)    // Friday (5)

        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(weeklyTask, monday))
        assertFalse(TaskRepositoryImpl.isTaskScheduledOnDate(weeklyTask, tuesday))
        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(weeklyTask, wednesday))
        assertFalse(TaskRepositoryImpl.isTaskScheduledOnDate(weeklyTask, thursday))
        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(weeklyTask, friday))
    }

    @Test
    fun testIntervalTaskScheduledEveryNDays() {
        val baseDate = LocalDate.of(2026, 9, 20)
        val baseTimeMs = baseDate.toEpochDay() * 24 * 60 * 60 * 1000

        val intervalTask = TaskWithDetails(
            task = TaskEntity(id = 3L, title = "深蹲訓練", createdAt = baseTimeMs, periodId = 3L),
            period = PeriodEntity(id = 3L, type = "INTERVAL", intervalDays = 2, displayTitle = "每 2 天"),
            hashTags = emptyList()
        )

        val day0 = baseDate
        val day1 = baseDate.plusDays(1)
        val day2 = baseDate.plusDays(2)
        val day3 = baseDate.plusDays(3)

        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(intervalTask, day0))
        assertFalse(TaskRepositoryImpl.isTaskScheduledOnDate(intervalTask, day1))
        assertTrue(TaskRepositoryImpl.isTaskScheduledOnDate(intervalTask, day2))
        assertFalse(TaskRepositoryImpl.isTaskScheduledOnDate(intervalTask, day3))
    }

    @Test
    fun testTaskWithDetailsToTaskCard() {
        val taskWithDetails = TaskWithDetails(
            task = TaskEntity(id = 100L, title = "記 100 個日文單詞", colorTheme = 0xFFE6F8F3, periodId = 1L),
            period = PeriodEntity(id = 1L, type = "DAILY", displayTitle = "每日"),
            hashTags = listOf(
                HashTagEntity(id = 1L, title = "語言學習", color = 0xFF2563EB),
                HashTagEntity(id = 2L, title = "核心詞彙", color = 0xFF059669)
            )
        )

        val card = taskWithDetails.toTaskCard(isCompletedOnDate = true)
        assertEquals(100L, card.id)
        assertEquals("記 100 個日文單詞", card.title)
        assertTrue(card.isCompleted)
        assertEquals("每日", card.period.displayTitle)
        assertEquals(2, card.hashTags.size)
        assertEquals("語言學習", card.hashTags[0].title)
    }
}
