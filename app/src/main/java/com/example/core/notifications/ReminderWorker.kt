package com.example.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker that shows a local notification for payment reminders.
 * Works completely offline - no backend required.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Payment Reminder"
        val message = inputData.getString(KEY_MESSAGE) ?: "You have a pending payment."
        val projectName = inputData.getString(KEY_PROJECT_NAME) ?: ""

        showNotification(title, if (projectName.isNotBlank()) "$message ($projectName)" else message)
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Payment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for pending payments"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "payment_reminders"
        const val KEY_TITLE = "reminder_title"
        const val KEY_MESSAGE = "reminder_message"
        const val KEY_PROJECT_NAME = "reminder_project_name"

        /**
         * Schedules a one-time payment reminder notification.
         * @param context Application context
         * @param delayMillis Delay from now in milliseconds
         * @param title Notification title
         * @param message Notification message
         * @param projectName Associated project name
         * @param tag Unique tag for this reminder (to allow cancellation)
         */
        fun scheduleReminder(
            context: Context,
            delayMillis: Long,
            title: String,
            message: String,
            projectName: String = "",
            tag: String
        ) {
            val inputData = Data.Builder()
                .putString(KEY_TITLE, title)
                .putString(KEY_MESSAGE, message)
                .putString(KEY_PROJECT_NAME, projectName)
                .build()

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(tag)
                .addTag(TAG_PAYMENT_REMINDER)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, request)
        }

        /**
         * Cancels a specific reminder by tag.
         */
        fun cancelReminder(context: Context, tag: String) {
            WorkManager.getInstance(context).cancelUniqueWork(tag)
        }

        /**
         * Cancels all payment reminders.
         */
        fun cancelAllReminders(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG_PAYMENT_REMINDER)
        }

        const val TAG_PAYMENT_REMINDER = "payment_reminder"
    }
}
