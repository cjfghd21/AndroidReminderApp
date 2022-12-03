package com.example.a436proj

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupBinding
import com.example.a436proj.databinding.ActivityNotificationsBinding
import java.io.Serializable
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.TimeSource

class NotificationsActivity : AppCompatActivity() {

    companion object {
        val requestCode : Int = 202
    }

    private final var purple : Int = Color.rgb(156, 39, 176)
    private lateinit var viewModel : NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.title = "Notification Settings"

        val binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        binding.btnNotificationSave.setOnClickListener {
            save(true, binding.timePicker)
        }

        binding.btnNotificationCancel.setOnClickListener {
            save(false, binding.timePicker)
        }

        binding.btnDaily.isChecked = true
        binding.btnDaily.setOnClickListener {
            if (binding.btnDaily.isChecked) {
                displayWeeklyButtons(binding, View.GONE)
            }
            viewModel.setDailyInterval()
        }

        // weekly buttons are hidden by default
        displayWeeklyButtons(binding, View.GONE)
        binding.btnWeekly.setOnClickListener {
            if (binding.btnWeekly.isChecked) {
                displayWeeklyButtons(binding, View.VISIBLE)
            }
            viewModel.setWeeklyInterval(DayOfWeek.MONDAY, 1)
        }

        // binding weekly buttons
        binding.btnSun.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.SUNDAY)
            viewModel.setWeeklyInterval(DayOfWeek.SUNDAY, 1)
        }

        binding.btnMon.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.MONDAY)
            viewModel.setWeeklyInterval(DayOfWeek.MONDAY, 1)
        }

        binding.btnTue.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.TUESDAY)
            viewModel.setWeeklyInterval(DayOfWeek.TUESDAY, 1)
        }

        binding.btnWed.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.WEDNESDAY)
            viewModel.setWeeklyInterval(DayOfWeek.WEDNESDAY, 1)
        }

        binding.btnThu.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.THURSDAY)
            viewModel.setWeeklyInterval(DayOfWeek.THURSDAY, 1)
        }

        binding.btnFri.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.FRIDAY)
            viewModel.setWeeklyInterval(DayOfWeek.FRIDAY, 1)
        }

        binding.btnSat.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.SATURDAY)
            viewModel.setWeeklyInterval(DayOfWeek.SATURDAY, 1)
        }

        Log.d("CREATION", "onCreateView is being called")
    }

    private fun displayWeeklyButtons(
        binding: ActivityNotificationsBinding,
        view: Int)
    {
        binding.btnSun.visibility = view
        binding.btnMon.visibility = view
        binding.btnTue.visibility = view
        binding.btnWed.visibility = view
        binding.btnThu.visibility = view
        binding.btnFri.visibility = view
        binding.btnSat.visibility = view
    }

    private fun setButtonBackgroundForDay(
        binding: ActivityNotificationsBinding,
        day: DayOfWeek)
    {
        binding.btnSun.setBackgroundColor(Color.GRAY)
        binding.btnMon.setBackgroundColor(Color.GRAY)
        binding.btnTue.setBackgroundColor(Color.GRAY)
        binding.btnWed.setBackgroundColor(Color.GRAY)
        binding.btnThu.setBackgroundColor(Color.GRAY)
        binding.btnFri.setBackgroundColor(Color.GRAY)
        binding.btnSat.setBackgroundColor(Color.GRAY)

        when (day) {
            DayOfWeek.SUNDAY -> {
                binding.btnSun.setBackgroundColor(Color.GREEN)
            }
            DayOfWeek.MONDAY -> {
                binding.btnMon.setBackgroundColor(purple)
            }
            DayOfWeek.TUESDAY -> {
                binding.btnTue.setBackgroundColor(purple)
            }
            DayOfWeek.WEDNESDAY -> {
                binding.btnWed.setBackgroundColor(purple)
            }
            DayOfWeek.THURSDAY -> {
                binding.btnThu.setBackgroundColor(purple)
            }
            DayOfWeek.FRIDAY -> {
                binding.btnFri.setBackgroundColor(purple)
            }
            DayOfWeek.SATURDAY -> {
                binding.btnSat.setBackgroundColor(purple)
            }
        }
    }

    private fun save(sendData: Boolean, timePicker: TimePicker) {
        if (sendData) {
            val intent = Intent()
            var interval: Interval = viewModel.getInterval()
            interval.timeToSendNotification = LocalTime.of(timePicker.hour, timePicker.minute, 0)
            interval.lastUpdateTimestamp = LocalDateTime.now()
            intent.putExtra("interval", interval as Serializable)
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}