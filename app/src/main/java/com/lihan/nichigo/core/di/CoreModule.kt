package com.lihan.nichigo.core.di

import com.lihan.nichigo.core.database.di.databaseModule
import org.koin.dsl.module

val coreModule = module {
    includes(databaseModule)
}
