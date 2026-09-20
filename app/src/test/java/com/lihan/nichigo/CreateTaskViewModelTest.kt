package com.lihan.nichigo

import com.lihan.nichigo.core.domain.util.DataError
import com.lihan.nichigo.core.domain.util.EmptyResult
import com.lihan.nichigo.core.domain.util.Result
import com.lihan.nichigo.task.domain.model.DayCompletionStatus
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard
import com.lihan.nichigo.task.domain.repository.TaskRepository
import com.lihan.nichigo.task.presentation.create.CreateTaskAction
import com.lihan.nichigo.task.presentation.create.CreateTaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: CreateTaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
        viewModel = CreateTaskViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testEmptyTitleSubmitSetsError() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        viewModel.onAction(CreateTaskAction.OnSubmit)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.titleError)
    }

    @Test
    fun testTitleChangeClearsError() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        viewModel.onAction(CreateTaskAction.OnSubmit)
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.titleError)

        viewModel.onAction(CreateTaskAction.OnTitleChange("New Task"))
        advanceUntilIdle()
        assertEquals("New Task", viewModel.state.value.title)
        assertNull(viewModel.state.value.titleError)
    }

    @Test
    fun testStepInterval() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        viewModel.onAction(CreateTaskAction.OnIntervalDaysChange(5))
        advanceUntilIdle()
        assertEquals(5, viewModel.state.value.intervalDays)

        viewModel.onAction(CreateTaskAction.OnStepInterval(1))
        advanceUntilIdle()
        assertEquals(6, viewModel.state.value.intervalDays)

        viewModel.onAction(CreateTaskAction.OnStepInterval(-1))
        advanceUntilIdle()
        assertEquals(5, viewModel.state.value.intervalDays)
    }

    @Test
    fun testSelectQuickWeekdays() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        val weekdays = setOf(1, 2, 3, 4, 5)
        viewModel.onAction(CreateTaskAction.OnSelectQuickWeekdays(weekdays))
        advanceUntilIdle()
        assertEquals(weekdays, viewModel.state.value.selectedDaysOfWeek)
    }

    @Test
    fun testAddNewTagMatchesExisting() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        val existing = HashTag(id = 1, title = "日語", color = 0L)
        fakeRepository.tagsFlow.value = listOf(existing)

        advanceUntilIdle()

        viewModel.onAction(CreateTaskAction.OnNewTagInputChange("#日語"))
        viewModel.onAction(CreateTaskAction.OnAddNewTag)
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.newTagInput)
        assertTrue(viewModel.state.value.selectedTags.any { it.title == "日語" })
    }

    private class FakeTaskRepository : TaskRepository {
        val tagsFlow = MutableStateFlow<List<HashTag>>(emptyList())

        override fun getAllHashTags(): Flow<List<HashTag>> = tagsFlow

        override suspend fun insertHashTag(title: String, color: Long): Result<HashTag, DataError.Local> {
            val newTag = HashTag(id = (tagsFlow.value.size + 1).toLong(), title = title, color = color)
            tagsFlow.value = tagsFlow.value + newTag
            return Result.Success(newTag)
        }

        override suspend fun createTask(
            title: String,
            period: Period,
            hashTags: List<HashTag>,
            colorTheme: Long
        ): Result<Long, DataError.Local> {
            return Result.Success(1L)
        }

        override fun getTasksForDate(date: LocalDate): Flow<List<TaskCard>> = MutableStateFlow(emptyList())

        override fun getMonthlyCompletionStatus(yearMonth: YearMonth): Flow<Map<LocalDate, DayCompletionStatus>> =
            MutableStateFlow(emptyMap())

        override suspend fun toggleTaskCompletion(taskId: Long, date: LocalDate): EmptyResult<DataError.Local> =
            Result.Success(Unit)
    }
}
