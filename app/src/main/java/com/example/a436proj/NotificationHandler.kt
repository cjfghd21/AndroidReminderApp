package com.example.a436proj

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

class NotificationHandler(alarmManager: AlarmManager, applicationContext: Context) : Serializable {

    private var groupNameToInterval: MutableMap<String, Interval> = mutableMapOf()
    private var alarmManager: AlarmManager
    private var applicationContext: Context

    init {
        this.alarmManager = alarmManager
        this.applicationContext = applicationContext
    }

    fun setIntervalForGroup(groupName: String, interval: Interval) {
        groupNameToInterval[groupName] = interval
    }

    fun cancelAllNotifications() {
        groupNameToInterval.forEach {entry ->
            val pendingIntent = createPendingIntent(entry.key)
            alarmManager.cancel(pendingIntent)
        }
        // clear information just in case
        groupNameToInterval = mutableMapOf()
    }

    fun scheduleNotification(groupName: String, interval: Interval) {
        val pendingIntent = createPendingIntent(groupName)

        // cancel if previously tied intent exists
        alarmManager.cancel(pendingIntent)

        // set new alarm
        val time = getTime(interval)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun createPendingIntent(groupName: String): PendingIntent {
        val intent = Intent(this.applicationContext, Notification::class.java)
        // set action with groupName so that intent's filterEquals is true for the same group.
        intent.setAction(groupName)
        intent.putExtra(content, String.format("Send notification to group %s", groupName))
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
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
                    if (current.toLocalTime().isAfter(interval.timeToSendNotification)) {
                        current = current.plusDays(7)
                    }
                }
            }
        }

        val date = current.dayOfMonth
        val month = current.monthValue
        val year = current.year
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, date)
        return calendar
    }
}