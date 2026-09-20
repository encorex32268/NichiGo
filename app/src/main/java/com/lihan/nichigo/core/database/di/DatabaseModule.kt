package com.lihan.nichigo.core.database.di

import androidx.room.Room
import com.lihan.nichigo.core.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "nichigo_task.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }
    single { get<AppDatabase>().taskDao() }
}
