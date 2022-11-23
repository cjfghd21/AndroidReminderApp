package com.example.a436proj

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val channelID = "channel1"
const val notificationID = 1
const val content = "content"

class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setAutoCancel(true)
            .setContentTitle(getContentTitle(intent))
            .setContentText(getContentText(intent))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

    private fun getContentTitle(intent: Intent): String {
        return "Reminder to send notification"
    }

    private fun getContentText(intent: Intent): String {
        return intent.getStringExtra(content)!!
    }
}