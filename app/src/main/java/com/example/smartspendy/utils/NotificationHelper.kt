package com.example.smartspendy.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.smartspendy.R

class NotificationHelper(private val application: Application) {

    private val BUDGET_CHANNEL_ID = "budget_alerts"
    private val DAILY_CHANNEL_ID = "daily_reminders"
    private val BUDGET_NOTIFICATION_ID = 1001
    private val DAILY_NOTIFICATION_ID = 1002

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Budget Alerts Channel
            val budgetChannel = NotificationChannel(
                BUDGET_CHANNEL_ID,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for budget nearing or exceeding alerts"
            }

            // Daily Reminders Channel
            val dailyChannel = NotificationChannel(
                DAILY_CHANNEL_ID,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to record expenses"
            }

            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(budgetChannel)
            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    fun sendBudgetAlert(message: String) {
        // Check for POST_NOTIFICATIONS permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, skip sending notification
            return
        }

        val isExceed = message.contains("exceeded", ignoreCase = true)
        val builder = NotificationCompat.Builder(application, BUDGET_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
            .setContentTitle(if (isExceed) "Budget Limit Exceeded!" else "Budget Alert")
            .setContentText(
                if (isExceed) "You've spent more than your set budget this month." else message
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Add heads-up configuration for exceed case
        if (isExceed) {
            builder.setVibrate(longArrayOf(0, 500, 500, 500)) // Vibration pattern for heads-up
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Ensure sound and visibility
        }

        with(NotificationManagerCompat.from(application)) {
            notify(BUDGET_NOTIFICATION_ID, builder.build())
        }
    }

    fun sendDailyReminder() {
        // Check for POST_NOTIFICATIONS permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, skip sending notification
            return
        }

        val builder = NotificationCompat.Builder(application, DAILY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
            .setContentTitle("Daily Expense Reminder")
            .setContentText("Don't forget to record your expenses for today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(application)) {
            notify(DAILY_NOTIFICATION_ID, builder.build())
        }
    }
}