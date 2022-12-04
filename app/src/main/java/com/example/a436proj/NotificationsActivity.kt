package com.example.a436proj

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityNotificationsBinding
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalTime

class NotificationsActivity : AppCompatActivity() {

    companion object {
        const val requestCode : Int = 202
    }

    private var purple : Int = Color.rgb(156, 39, 176)
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
            if (binding.btnDaily.isChecked) {
                displayWeeklyButtons(binding, View.GONE)
            }
            viewModel.setDailyInterval()
        }

        binding.btnWeekly.setOnClickListener {
            if (binding.btnWeekly.isChecked) {
                displayWeeklyButtons(binding, View.VISIBLE)
            }
            viewModel.setWeeklyInterval(DayOfWeek.MONDAY, 1)
        }

        // binding weekly buttons
        binding.btnSun.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.SUNDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.SUNDAY)
        }

        binding.btnMon.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.MONDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.MONDAY)
        }

        binding.btnTue.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.TUESDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.TUESDAY)
        }

        binding.btnWed.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.WEDNESDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.WEDNESDAY)
        }

        binding.btnThu.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.THURSDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.THURSDAY)
        }

        binding.btnFri.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.FRIDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.FRIDAY)
        }

        binding.btnSat.setOnClickListener {
            setButtonBackgroundForDay(binding, DayOfWeek.SATURDAY)
            viewModel.setWeeklyIntervalDay(DayOfWeek.SATURDAY)
        }

        // set up spinner for weekly custom
        val spinner: Spinner = findViewById(R.id.weekly_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.weekly_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.weeklySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setWeeklyIntervalValue(position + 1)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val interval : Interval = viewModel.getInterval()
        if (interval.intervalType == IntervalType.Daily) {
            binding.btnDaily.isChecked = true
            displayWeeklyButtons(binding, View.GONE)
        } else if (interval.intervalType == IntervalType.Weekly) {
            binding.btnWeekly.isChecked = true
            displayWeeklyButtons(binding, View.VISIBLE)
            setButtonBackgroundForDay(binding, interval.weeklyInterval.day)
            spinner.setSelection(interval.weeklyInterval.weekInterval - 1)
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
        binding.weeklySpinner.visibility = view
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
                binding.btnSun.setBackgroundColor(purple)
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
            intent.putExtra("interval", interval as Serializable)
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}