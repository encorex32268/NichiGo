package com.lihan.nichigo.task.di

import com.lihan.nichigo.task.data.repository.TaskRepositoryImpl
import com.lihan.nichigo.task.domain.repository.TaskRepository
import com.lihan.nichigo.task.presentation.calendar.CalendarViewModel
import com.lihan.nichigo.task.presentation.create.CreateTaskViewModel
import com.lihan.nichigo.task.presentation.home.TaskViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val taskModule = module {
    singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
    viewModelOf(::TaskViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::CreateTaskViewModel)
}
