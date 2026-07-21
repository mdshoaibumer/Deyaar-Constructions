package com.example.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PinScreen(
    title: String,
    onSuccess: () -> Unit
) {
    // This is the Application Shell placeholder for PIN entry / Biometrics.
    // Real implementation would have a custom numeric keyboard and check the PIN against DataStore.
    // It would also prompt Biometrics if available.
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        Button(onClick = onSuccess) {
            Text("Simulate Unlock")
        }
    }
}
