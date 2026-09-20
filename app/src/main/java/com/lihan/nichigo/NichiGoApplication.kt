package com.lihan.nichigo

import android.app.Application
import com.lihan.nichigo.core.di.coreModule
import com.lihan.nichigo.task.di.taskModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NichiGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@NichiGoApplication)
            modules(coreModule, taskModule)
        }
    }
}
