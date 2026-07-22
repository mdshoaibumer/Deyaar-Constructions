package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.security.SecurityPreferences
import com.example.ui.theme.Dimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PinScreen(
    title: String,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val securityPrefs = remember { SecurityPreferences(context) }
    val isPinSet = securityPrefs.isPinEnabled()

    // If PIN is not set, skip directly (first time setup or PIN disabled)
    if (!isPinSet && title.contains("Unlock", ignoreCase = true)) {
        LaunchedEffect(Unit) { onSuccess() }
        return
    }

    var enteredPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val isSetupMode = title.contains("Setup", ignoreCase = true) || !isPinSet

    val currentTitle = when {
        isSetupMode && !isConfirming -> "Create PIN"
        isSetupMode && isConfirming -> "Confirm PIN"
        else -> title
    }

    // Shake animation for error
    val shakeOffset = remember { Animatable(0f) }

    // Dot scale animations
    val dotScales = remember { List(4) { Animatable(1f) } }

    // Animate dot when filled
    val currentPin = if (isConfirming) confirmPin else enteredPin
    LaunchedEffect(currentPin.length) {
        if (currentPin.isNotEmpty()) {
            val index = currentPin.length - 1
            if (index in dotScales.indices) {
                dotScales[index].animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
                dotScales[index].animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    fun triggerShake() {
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            // Quick shake left-right-left-right-center
            val offsets = listOf(10f, -10f, 8f, -8f, 4f, -4f, 0f)
            for (offset in offsets) {
                shakeOffset.animateTo(offset, animationSpec = tween(50))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spaceLarge)
            .semantics { contentDescription = currentTitle },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DEYAAR",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge))

        Text(
            text = currentTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Dimens.spaceLarge))

        // PIN dots with animation
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.graphicsLayer {
                translationX = shakeOffset.value
            }
        ) {
            repeat(4) { index ->
                val isFilled = index < currentPin.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .scale(dotScales[index].value)
                        .clip(CircleShape)
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                        .semantics {
                            contentDescription = if (isFilled) "Digit entered" else "Empty"
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

        // Error message with animation
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge))

        // Number Pad
        val numbers = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        numbers.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.size(72.dp))
                    } else if (key == "DEL") {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isConfirming && confirmPin.isNotEmpty()) {
                                    confirmPin = confirmPin.dropLast(1)
                                } else if (!isConfirming && enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                }
                                error = null
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .semantics { contentDescription = "Delete last digit" }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                error = null
                                if (isConfirming) {
                                    if (confirmPin.length < 4) {
                                        confirmPin += key
                                        if (confirmPin.length == 4) {
                                            if (confirmPin == enteredPin) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                securityPrefs.setPin(confirmPin)
                                                onSuccess()
                                            } else {
                                                error = "PINs don't match. Try again."
                                                triggerShake()
                                                confirmPin = ""
                                                enteredPin = ""
                                                isConfirming = false
                                            }
                                        }
                                    }
                                } else {
                                    if (enteredPin.length < 4) {
                                        enteredPin += key
                                        if (enteredPin.length == 4) {
                                            if (isSetupMode) {
                                                isConfirming = true
                                            } else {
                                                // Verify PIN
                                                if (securityPrefs.verifyPin(enteredPin)) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    onSuccess()
                                                } else {
                                                    error = "Incorrect PIN"
                                                    triggerShake()
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .semantics { contentDescription = "Digit $key" },
                            shape = CircleShape
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        }

        // Skip option for first-time setup
        if (isSetupMode) {
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            TextButton(onClick = onSuccess) {
                Text("Set up later in Settings")
            }
        }
    }
}
