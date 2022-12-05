package com.example.a436proj

import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalTime

class NotificationsViewModel : ViewModel() {

    private var interval: Interval = Interval(IntervalType.Daily, LocalTime.now())

    fun setDailyInterval() {
        interval.intervalType = IntervalType.Daily
    }

    fun setTime(hour: Int, minute: Int) {
        interval.timeToSendNotification = LocalTime.of(hour, minute)
    }

    fun setWeeklyInterval(day: DayOfWeek, weekInterval: Int) {
        interval.intervalType = IntervalType.Weekly
        interval.weeklyInterval = WeeklyInterval(day, weekInterval)
    }

    fun setWeeklyIntervalDay(day: DayOfWeek) {
        interval.weeklyInterval.day = day
    }

    fun setWeeklyIntervalValue(intervalValue: Int) {
        interval.weeklyInterval.weekInterval = intervalValue
    }

    fun getInterval(): Interval  {
        return interval
    }
}