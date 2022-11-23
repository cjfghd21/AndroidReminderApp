package com.example.a436proj

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDateTime

enum class IntervalType(val printableName: String) {
    Daily("Daily"),
    Weekly("Weekly"),
    Monthly("Monthly")
}

data class Interval (var intervalType: IntervalType,
                     var timeToSendNotification: Time) : Serializable {
    var lastUpdateTimestamp: Long = System.currentTimeMillis();
    var weeklyInterval: WeeklyInterval = WeeklyInterval(DayOfWeek.MONDAY)
    var monthlyInterval: MonthlyInterval = MonthlyInterval(1)
}

data class WeeklyInterval (var day: DayOfWeek) : Serializable {

}

data class MonthlyInterval (var date: Long) : Serializable {

}