package com.udacity.project4.di

import android.annotation.SuppressLint
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

@SuppressLint("Registered")
class KoinTestApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
           androidContext(this@KoinTestApp)
            modules(emptyList())
        }
    }

    internal fun injectModule(module: Module) {
        loadKoinModules(module)
    }

    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }

}



