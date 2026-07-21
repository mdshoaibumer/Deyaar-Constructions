package com.example.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

object SessionManager {
    private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes

    private var lastActiveTime: Long = Instant.now().toEpochMilli()

    private val _isSessionLocked = MutableStateFlow(false)
    val isSessionLocked: StateFlow<Boolean> = _isSessionLocked.asStateFlow()

    fun updateLastActiveTime() {
        lastActiveTime = Instant.now().toEpochMilli()
    }

    fun checkSessionTimeout() {
        val now = Instant.now().toEpochMilli()
        if (now - lastActiveTime > SESSION_TIMEOUT_MS) {
            lockSession()
        }
    }

    fun lockSession() {
        _isSessionLocked.value = true
    }

    fun unlockSession() {
        _isSessionLocked.value = false
        updateLastActiveTime()
    }
}
