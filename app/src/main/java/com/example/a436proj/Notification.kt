package com.example.a436proj

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC

const val channelID = "channel1"
const val notificationID = 1
const val interval = "interval"

class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {
        System.out.println("received sth")

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setAutoCancel(true)
            .setContentTitle(getContentTitle(intent))
            .setContentText(getContentText(intent))
            .setVisibility(VISIBILITY_PUBLIC)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

    private fun getContentTitle(intent: Intent): String {
        return "For now Title"
    }

    private fun getContentText(intent: Intent): String {
        return "For now text"
    }
}