package com.lihan.nichigo

import app.cash.turbine.test
import com.lihan.nichigo.core.domain.util.DataError
import com.lihan.nichigo.core.domain.util.EmptyResult
import com.lihan.nichigo.core.domain.util.Result
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.domain.repository.TaskRepository
import com.lihan.nichigo.task.presentation.home.TaskAction
import com.lihan.nichigo.task.presentation.home.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = kotlinx.coroutines.test.UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
        viewModel = TaskViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state collects tasks and calculates progress accurately`() = runTest(testDispatcher) {
        val testTasks = listOf(
            TaskCard(
                id = 1L,
                title = "記 100 個日文單詞",
                isCompleted = true,
                colorTheme = 0xFFE6F8F3,
                period = Period(1L, PeriodType.DAILY, 1, emptyList(), "每日"),
                hashTags = emptyList()
            ),
            TaskCard(
                id = 2L,
                title = "深蹲與核心訓練",
                isCompleted = false,
                colorTheme = 0xFFEFF4FF,
                period = Period(2L, PeriodType.INTERVAL, 2, emptyList(), "每 2 天"),
                hashTags = emptyList()
            )
        )

        fakeRepository.emitTasks(testTasks)

        viewModel.state.test {
            // initial or emitted item
            var item = awaitItem()
            if (item.isLoading) {
                item = awaitItem()
            }
            assertEquals(2, item.totalCount)
            assertEquals(1, item.completedCount)
            assertEquals(0.5f, item.progress, 0.01f)
            assertEquals(2, item.tasks.size)
            // Verify sorting: uncompleted task (id=2) first, completed task (id=1) at the bottom
            assertEquals(2L, item.tasks[0].id)
            assertFalse(item.tasks[0].isCompleted)
            assertEquals(1L, item.tasks[1].id)
            assertTrue(item.tasks[1].isCompleted)
        }
    }

    @Test
    fun `onAction toggle task invokes repository toggle`() = runTest(testDispatcher) {
        viewModel.onAction(TaskAction.OnToggleTask(1L))
        testScheduler.advanceUntilIdle()
        assertTrue(fakeRepository.toggledTaskIds.contains(1L))
    }
}

class FakeTaskRepository : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<TaskCard>>(emptyList())
    val toggledTaskIds = mutableListOf<Long>()

    fun emitTasks(tasks: List<TaskCard>) {
        tasksFlow.value = tasks
    }

    override fun getTasksForDate(date: LocalDate): Flow<List<TaskCard>> = tasksFlow.asStateFlow()

    override fun getMonthlyCompletionStatus(yearMonth: YearMonth): Flow<Map<LocalDate, DayCompletionStatus>> =
        MutableStateFlow(emptyMap())

    override suspend fun toggleTaskCompletion(taskId: Long, date: LocalDate): EmptyResult<DataError.Local> {
        toggledTaskIds.add(taskId)
        return Result.Success(Unit)
    }

    override fun getAllHashTags(): Flow<List<HashTag>> = MutableStateFlow(emptyList())

    override suspend fun insertHashTag(title: String, color: Long): Result<HashTag, DataError.Local> =
        Result.Success(HashTag(1L, title, color))

    override suspend fun createTask(
        title: String,
        period: Period,
        hashTags: List<HashTag>,
        colorTheme: Long
    ): Result<Long, DataError.Local> = Result.Success(1L)
}
