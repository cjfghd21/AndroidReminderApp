package com.example.a436proj

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.a436proj.NotificationHandler.LocalBinder
import java.util.*

const val channelID = "channel1"
const val intervalKey = "interval"
const val groupNameKey = "groupName"

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setContentTitle(getContentTitle())
            .setContentText(getContentText(intent))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Calendar.getInstance().timeInMillis.toInt(), notification)

        // find service and reschedule notification
        val serviceIntent = Intent(context, NotificationHandler::class.java)
        context.startService(serviceIntent)
        val binder = peekService(context, Intent(context, NotificationHandler::class.java))
        if (binder != null) {
            val notificationHandler = (binder as LocalBinder).getService()
            notificationHandler.scheduleNotification(getGroupName(intent), getInterval(intent))
        }
    }

    private fun getContentTitle(): String {
        return "Call Your Mother"
    }

    private fun getContentText(intent: Intent): String {
        return String.format("It's time to contact group %s", getGroupName(intent))
    }

    private fun getGroupName(intent: Intent): String {
        return intent.getStringExtra(groupNameKey)!!
    }

    private fun getInterval(intent: Intent): Interval {
        return intent.getSerializableExtra(intervalKey) as Interval
    }
}