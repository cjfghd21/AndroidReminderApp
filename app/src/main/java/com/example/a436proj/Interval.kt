package com.example.a436proj

import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalTime

enum class IntervalType(val printableName: String) {
    Daily("Daily"),
    Weekly("Weekly")
}

data class Interval (var intervalType: IntervalType,
                     var timeToSendNotification: LocalTime) : Serializable {
    var weeklyInterval: WeeklyInterval = WeeklyInterval(DayOfWeek.MONDAY, 1)
}

data class WeeklyInterval (var day: DayOfWeek,
                           var weekInterval: Int) : Serializable {}