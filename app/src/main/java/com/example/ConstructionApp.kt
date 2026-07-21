package com.example

import android.app.Application
import com.example.di.AppContainer
import timber.log.Timber

class ConstructionApp : Application() {
    
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        
        container = AppContainer(this)
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
