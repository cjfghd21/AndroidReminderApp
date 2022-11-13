package com.example.a436proj.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.a436proj.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.MessageFormat
import android.media.AudioAttributes
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService

import com.example.a436proj.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {

        /** Notification ID to allow for future updates. */
        private const val NOTIFICATION_ID = 1

        /** Channel identifier. */
        private const val CHANNEL_ID = ".channel-01"

    }
    private lateinit var channelID: String

    private lateinit var notificationManager: NotificationManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        createNotificationChannel()
        _binding!!.btnNotification.setOnClickListener{
            postNotification()
        }
        Log.d("CREATION", "onCreateView is being called")
        return root
    }

    private fun createNotificationChannel() {
        // The channel ID to package name with an ID suffix.
        // channelID = packageName + CHANNEL_ID
        channelID = CHANNEL_ID

        // Step 1: Create and initialize a notification channel.
        // Step 2: Configure the notification channel
        NotificationChannel(
            channelID,
            getString(R.string.channel_name), // visible to user
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // Configure the notification channel.
            description = description
            enableLights(true)
        }.also {
            // Step 3: Pass this channel to the NotificationManager.
            notificationManager =
                getActivity()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(it)
        }
    }

    private fun postNotification() {
        val contentIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            Intent(requireContext(), javaClass),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the Notification.
        val notification = Notification.Builder(requireContext(), channelID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setAutoCancel(true)
            .setContentTitle("Testing")
            .setContentText("Content")
            .setContentIntent(contentIntent)
            .build()

        // Pass the Notification to the NotificationManager:
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}