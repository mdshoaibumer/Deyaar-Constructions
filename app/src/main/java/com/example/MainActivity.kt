package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.layout.AppScaffold
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.core.security.SecurityManager
import com.example.core.security.SecurityPreferences

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val securityPreferences = SecurityPreferences(this)
        val securityManager = SecurityManager(this)
        
        setContent {
            MyApplicationTheme {
                var isUnlocked by remember { mutableStateOf(!securityPreferences.isAppLockEnabled) }
                var authError by remember { mutableStateOf<String?>(null) }
                
                if (isUnlocked) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    val navController = rememberNavController()
                    val appContainer = (application as ConstructionApp).container

                    AppScaffold(
                        navController = navController,
                        windowSizeClass = windowSizeClass.widthSizeClass
                    ) { paddingValues ->
                        AppNavigation(
                            navController = navController,
                            paddingValues = paddingValues,
                            appContainer = appContainer
                        )
                    }
                } else {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "App is Locked", style = MaterialTheme.typography.headlineMedium)
                            if (authError != null) {
                                Text(text = authError!!, color = MaterialTheme.colorScheme.error)
                            }
                            Button(onClick = {
                                securityManager.authenticate(
                                    activity = this@MainActivity,
                                    onSuccess = { isUnlocked = true },
                                    onError = { authError = it }
                                )
                            }) {
                                Text("Unlock")
                            }
                        }
                    }
                    
                    // Trigger auth immediately on launch
                    LaunchedEffect(Unit) {
                        securityManager.authenticate(
                            activity = this@MainActivity,
                            onSuccess = { isUnlocked = true },
                            onError = { authError = it }
                        )
                    }
                }
            }
        }
    }
}
