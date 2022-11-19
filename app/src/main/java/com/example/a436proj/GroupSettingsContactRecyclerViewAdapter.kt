package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.GroupSettingsContactBinding

class GroupSettingsContactRecyclerViewAdapter(var context: Context, var contactsList : MutableList<SelectableGroups.Group.Contact>, var handleTickCheckbox : (position : Int) -> Unit) : RecyclerView.Adapter<GroupSettingsContactRecyclerViewAdapter.GroupSettingsContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSettingsContactViewHolder {
        return GroupSettingsContactViewHolder(GroupSettingsContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: GroupSettingsContactViewHolder, position: Int) {
        holder.name.text = contactsList[position].name
        holder.phoneNumber.text = contactsList[position].phoneNumber
        holder.checkBox.setOnClickListener {
            handleTickCheckbox(position)
        }

        holder.checkBox.isChecked = contactsList[position].groupSettingsIsChecked
    }

    override fun getItemCount(): Int = contactsList.size

    fun updateContactsList(newList : MutableList<SelectableGroups.Group.Contact>) {
        contactsList = newList
        notifyDataSetChanged()
    }

    inner class GroupSettingsContactViewHolder(binding: GroupSettingsContactBinding) : RecyclerView.ViewHolder(binding.root) {
        val name : TextView = binding.name
        val phoneNumber = binding.phoneNumber
        val checkBox = binding.contactCheckbox

    }
}