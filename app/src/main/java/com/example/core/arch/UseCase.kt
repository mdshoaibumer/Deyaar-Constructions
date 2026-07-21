package com.example.core.arch

import com.example.core.error.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import com.example.core.error.AppError
import timber.log.Timber

/**
 * Base UseCase for suspending operations.
 * Executes on the provided dispatcher and wraps exceptions into [Result.Failure].
 */
abstract class SuspendUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters).let {
                    Result.Success(it)
                }
            }
        } catch (e: AppError) {
            Timber.e(e, "UseCase Execution Failed")
            Result.Failure(e)
        } catch (e: Exception) {
            Timber.e(e, "UseCase Execution Failed with Unknown Exception")
            Result.Failure(AppError.UnknownError(e.message ?: "Unknown error occurred", e))
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * Base UseCase for observing data streams (Flow).
 */
abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(parameters: P): Flow<Result<R>> = execute(parameters)
        .catch { e -> 
            Timber.e(e, "FlowUseCase encountered an error")
            if (e is AppError) {
                emit(Result.Failure(e))
            } else {
                emit(Result.Failure(AppError.UnknownError(e.message ?: "Unknown error", e)))
            }
        }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameters: P): Flow<Result<R>>
}
