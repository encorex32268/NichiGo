package com.lihan.nichigo.core.di

import com.lihan.nichigo.task.data.database.AppDatabase
import com.lihan.nichigo.task.data.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single<AppDatabase> { DatabaseFactory.create(androidContext()) }
    single { get<AppDatabase>().taskDao() }
}
