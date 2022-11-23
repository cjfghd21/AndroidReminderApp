package com.example.a436proj

import android.app.NotificationManager
import android.content.Intent
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

        binding.radioButton1.setOnClickListener {
            viewModel.setDailyInterval()
        }

        binding.radioButton2.setOnClickListener {
            viewModel.setWeeklyInterval(DayOfWeek.MONDAY)
        }

        binding.radioButton3.setOnClickListener {
            viewModel.setMonthlyInterval(binding.calendarView.date)
        }
        Log.d("CREATION", "onCreateView is being called")
    }

    private fun save(sendData: Boolean, timePicker: TimePicker) {
        if (sendData) {
            val intent = Intent()
            var interval: Interval = viewModel.getInterval()
            interval.timeToSendNotification = Time(timePicker.hour, timePicker.minute, 0)
            interval.lastUpdateTimestamp = System.currentTimeMillis();
            intent.putExtra("interval", interval as Serializable)
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}