package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.core.security.SecurityManager
import com.example.core.security.SecurityPreferences
import com.example.ui.components.layout.AppScaffold
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.Dimens
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val securityPreferences = SecurityPreferences(this)
        val securityManager = SecurityManager(this)

        setContent {
            val themePreferences = (application as ConstructionApp).themePreferences
            val isDarkMode by themePreferences.isDarkModeEnabled.collectAsState(initial = false)

            MyApplicationTheme(darkTheme = isDarkMode) {
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
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimens.spaceExtraLarge),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                            Text(
                                text = "DEYAAR",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                            Text(
                                text = "App is locked",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AnimatedVisibility(
                                visible = authError != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = authError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = Dimens.spaceMedium)
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge))

                            FilledTonalButton(
                                onClick = {
                                    authError = null
                                    securityManager.authenticate(
                                        activity = this@MainActivity,
                                        onSuccess = { isUnlocked = true },
                                        onError = { authError = it }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
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
