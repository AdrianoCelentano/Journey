package com.adriano.journey

import android.app.Application
import com.adriano.journey.di.initKoin
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext

class JourneyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initKoin {
            androidContext(this@JourneyApp)
        }
    }
}
