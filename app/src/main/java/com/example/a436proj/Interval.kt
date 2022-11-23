package com.example.a436proj

import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

enum class IntervalType(val printableName: String) {
    Daily("Daily"),
    Weekly("Weekly"),
    Monthly("Monthly")
}

data class Interval (var intervalType: IntervalType,
                     var timeToSendNotification: LocalTime) : Serializable {
    var lastUpdateTimestamp: LocalDateTime = LocalDateTime.now()
    var weeklyInterval: WeeklyInterval = WeeklyInterval(DayOfWeek.MONDAY)
    var monthlyInterval: MonthlyInterval = MonthlyInterval(1)
}

data class WeeklyInterval (var day: DayOfWeek) : Serializable {}

data class MonthlyInterval (var date: Long) : Serializable {}