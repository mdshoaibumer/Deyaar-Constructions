package com.example.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes

    private var lastActiveTime: Long = System.currentTimeMillis()

    private val _isSessionLocked = MutableStateFlow(false)
    val isSessionLocked: StateFlow<Boolean> = _isSessionLocked.asStateFlow()

    fun updateLastActiveTime() {
        lastActiveTime = System.currentTimeMillis()
    }

    fun checkSessionTimeout() {
        val now = System.currentTimeMillis()
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
