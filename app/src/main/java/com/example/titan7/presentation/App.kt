package com.example.titan7.presentation

import android.app.Application
import com.example.titan7.di.TitanModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() {
        org.koin.core.context.startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                TitanModule
            )
        }
    }
}