package com.example.a436proj

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
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

        binding.btnDaily.setOnClickListener {
            viewModel.setDailyInterval()
        }

        binding.btnWeekly.setOnClickListener {
            // default to Wednesday for now
            viewModel.setWeeklyInterval(DayOfWeek.WEDNESDAY, 1)

            // implement show & hide day of week buttons
        }

        // binding weekly buttons
        binding.btnSun.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.SUNDAY, 1)
            binding.btnSun.setBackgroundColor(Color.GRAY);
        }

        binding.btnMon.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.MONDAY, 1)
        }

        binding.btnTue.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.TUESDAY, 1)
        }

        binding.btnWed.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.WEDNESDAY, 1)
        }

        binding.btnThu.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.THURSDAY, 1)
        }

        binding.btnFri.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.FRIDAY, 1)
        }

        binding.btnSat.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.SATURDAY, 1)
        }

        Log.d("CREATION", "onCreateView is being called")
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