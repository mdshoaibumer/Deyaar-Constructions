package com.example.core.error

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>
    data object Loading : Result<Nothing>
    data object Empty : Result<Nothing>
}

sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class DatabaseError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class ValidationError(message: String, val fields: Map<String, String> = emptyMap()) : AppError(message)
    class BackupError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class StorageError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class SecurityError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class UnknownError(message: String, cause: Throwable? = null) : AppError(message, cause)
}
