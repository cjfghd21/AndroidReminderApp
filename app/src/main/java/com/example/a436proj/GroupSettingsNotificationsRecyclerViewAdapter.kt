package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.GroupSettingsContactBinding
import com.example.a436proj.databinding.GroupSettingsNotificationBinding

class GroupSettingsNotificationsRecyclerViewAdapter (var context: Context, var notificationList : MutableList<String>) : RecyclerView.Adapter<GroupSettingsNotificationsRecyclerViewAdapter.GroupSettingsNotificationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSettingsNotificationsRecyclerViewAdapter.GroupSettingsNotificationsViewHolder {
        return GroupSettingsNotificationsViewHolder(GroupSettingsNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: GroupSettingsNotificationsRecyclerViewAdapter.GroupSettingsNotificationsViewHolder, position: Int) {
        holder.notificationTextView.text = notificationList[position]

    }

    override fun getItemCount(): Int = notificationList.size

    inner class GroupSettingsNotificationsViewHolder(binding: GroupSettingsNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        var notificationTextView : TextView = binding.notification
    }
}