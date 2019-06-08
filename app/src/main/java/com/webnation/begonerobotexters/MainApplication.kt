package com.webnation.begonerobotexters

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule)
        }

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree());
        else {
            Timber.plant()
        }

    }

}