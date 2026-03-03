package com.adriano.journey

import android.app.Application
import com.adriano.journey.di.initKoin
import org.koin.android.ext.koin.androidContext

class JourneyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@JourneyApp)
        }
    }
}
