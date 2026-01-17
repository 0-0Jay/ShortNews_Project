package com.example.shortnews.ui

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import com.example.shortnews.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationIconViewModel : ViewModel() {

    object NotificationHelper {
        var id = 0;
        fun showNotification(context: Context, title: String,pendingIntent : PendingIntent) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "Shortnews")
            notification.setSmallIcon(R.drawable.logo)
            notification.setContentTitle(title)
            notification.setContentText(formatTime(System.currentTimeMillis(), "yyyy.MM.dd HH:mm:ss"))
            notification.setPriority(NotificationCompat.PRIORITY_HIGH)
            notification.setFullScreenIntent(pendingIntent, true)
            notification.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            notificationManager.notify(this.id++, notification.build())
        }

        fun formatTime(timestamp: Long, pattern: String): String {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            return sdf.format(date)
        }
    }
}

