package com.example.a436proj

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalTime

class NotificationsViewModel : ViewModel() {

    private var interval: Interval = Interval(IntervalType.Daily, LocalTime.of(0, 0))

    fun setDailyInterval() {
        interval.intervalType = IntervalType.Daily
    }

    fun setWeeklyInterval(day: DayOfWeek) {
        interval.intervalType = IntervalType.Weekly
        interval.weeklyInterval = WeeklyInterval(day)
    }

    fun setMonthlyInterval(date: Long) {
        interval.intervalType = IntervalType.Monthly
        interval.monthlyInterval = MonthlyInterval(1)
    }

    fun getInterval(): Interval  {
        return interval
    }
}