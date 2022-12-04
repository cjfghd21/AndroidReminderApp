package com.example.a436proj

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.time.LocalDateTime
import java.util.*

class NotificationHandler : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): NotificationHandler = this@NotificationHandler
    }
    private val binder = LocalBinder()
    private var groupNameToInterval: MutableMap<String, Interval> = mutableMapOf()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun setIntervalForGroup(groupName: String, interval: Interval) {
        groupNameToInterval[groupName] = interval
    }

    fun cancelAllNotifications() {
        groupNameToInterval.forEach {entry ->
            val pendingIntent = createPendingIntent(entry.key, entry.value)
            getAlarmManager().cancel(pendingIntent)
        }
        // clear information just in case
        groupNameToInterval = mutableMapOf()
    }

    fun scheduleNotification(groupName: String, interval: Interval) {
        val pendingIntent = createPendingIntent(groupName, interval)

        // cancel if previously tied intent exists
        getAlarmManager().cancel(pendingIntent)

        // set new alarm
        val time = getTime(interval)
        getAlarmManager().setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getAlarmManager() : AlarmManager{
        return getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun createPendingIntent(groupName: String, interval: Interval): PendingIntent {
        val intent = Intent(this.applicationContext, Notification::class.java)
        // set action with groupName so that intent's filterEquals is true for the same group.
        intent.action = groupName
        intent.putExtra(intervalKey, interval)
        intent.putExtra(groupNameKey, groupName)
        return PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getTime(interval: Interval): Long {
        val minute = interval.timeToSendNotification.minute
        val hour = interval.timeToSendNotification.hour
        val calendar = getCalendar(interval)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        return calendar.timeInMillis
    }

    private fun getCalendar(interval: Interval): Calendar {
        var current = LocalDateTime.now()

        when(interval.intervalType) {
            IntervalType.Daily -> {
                if (current.toLocalTime().isAfter(interval.timeToSendNotification)) {
                    current = current.plusDays(1)
                }
            }
            IntervalType.Weekly -> {
                if (current.dayOfWeek > interval.weeklyInterval.day) {
                    current = current.plusDays((interval.weeklyInterval.day.value - current.dayOfWeek.value + 7).toLong())
                } else if (current.dayOfWeek < interval.weeklyInterval.day) {
                    current = current.plusDays((interval.weeklyInterval.day.value - current.dayOfWeek.value).toLong())
                } else {
                    // current & interval are the same day
                    if (current.toLocalTime().isAfter(interval.timeToSendNotification)) {
                        current = current.plusDays((7 * interval.weeklyInterval.weekInterval).toLong())
                    }
                }
            }
        }

        val date = current.dayOfMonth
        val month = current.monthValue
        val year = current.year
        val calendar = Calendar.getInstance()
        // month for some reason start from 1 not 0
        calendar.set(year, month - 1, date)
        return calendar
    }
}