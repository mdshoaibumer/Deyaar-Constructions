package com.example

import android.app.Application
import com.example.core.preferences.ThemePreferences
import com.example.data.seed.DemoDataSeeder
import com.example.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class ConstructionApp : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var container: AppContainer
        private set

    lateinit var themePreferences: ThemePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        
        container = AppContainer(this)
        themePreferences = ThemePreferences(this)
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Seed demo data on first launch
        applicationScope.launch {
            try {
                DemoDataSeeder(container.database).seedIfEmpty()
            } catch (e: Exception) {
                Timber.e(e, "Demo data seeding failed")
            }
        }
    }
}
