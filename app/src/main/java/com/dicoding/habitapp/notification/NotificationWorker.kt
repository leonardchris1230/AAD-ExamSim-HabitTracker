package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)
        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify ) {
            if (habitTitle != null) {
                showNotif(ctx, habitId, habitTitle)
            }
        }

        return Result.success()
    }

    fun showNotif(context: Context, id: Int, title: String) {
        val intent = Intent(context, DetailHabitActivity::class.java).apply {
            putExtra(HABIT_ID, id)
        }
        val pendingIntent = TaskStackBuilder.create(context).run { addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManagerCompat =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(ctx.getString(R.string.notify_content))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "channelName",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notif = builder.build()
        notificationManagerCompat.notify(100, notif)

    }

}
