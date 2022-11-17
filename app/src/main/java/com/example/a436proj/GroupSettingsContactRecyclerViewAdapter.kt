package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.GroupSettingsContactBinding

class GroupSettingsContactRecyclerViewAdapter(var context: Context, var contactsList : MutableList<SelectableGroups.Group.Contact>) : RecyclerView.Adapter<GroupSettingsContactRecyclerViewAdapter.GroupSettingsContactViewHolder>() {

    var allSelected = false
    var allTheSame = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSettingsContactViewHolder {
        return GroupSettingsContactViewHolder(GroupSettingsContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: GroupSettingsContactViewHolder, position: Int) {
        holder.name.text = contactsList[position].name
        holder.timeSinceLastCall.text = contactsList[position].timeSinceLastCall
        holder.timeSinceLastText.text = contactsList[position].timeSinceLastText

        holder.checkBox.setOnClickListener {

            contactsList[position].groupSettingsIsChecked = holder.checkBox.isChecked

            allTheSame = true
            var checkedFound = false
            var notCheckedFound = false

            for (i in 0 until contactsList.size) {
                if (contactsList[i].groupSettingsIsChecked) {
                    checkedFound = true
                } else {
                    notCheckedFound = true
                }
            }

            if (checkedFound.xor(notCheckedFound)) {
                allTheSame = false
            }
        }

        if (allSelected) {
            holder.checkBox.isChecked = true
        }
        else {
            if (!allTheSame && contactsList[position].groupSettingsIsChecked) {
                holder.checkBox.isChecked = true
            }
            else if (!allTheSame && !contactsList[position].groupSettingsIsChecked) {
                holder.checkBox.isChecked = false
            }
            else {
                holder.checkBox.isChecked = false
            }
        }
    }

    override fun getItemCount(): Int = contactsList.size

    fun selectAll() {
        allSelected = true
        allTheSame = true
    }

    fun unselectAll() {
        allSelected = false
        allTheSame = true
    }

    inner class GroupSettingsContactViewHolder(binding: GroupSettingsContactBinding) : RecyclerView.ViewHolder(binding.root) {
        val name : TextView = binding.name
        val timeSinceLastCall : TextView = binding.timeSinceLastCall
        val timeSinceLastText : TextView = binding.timeSinceLastText
        val checkBox = binding.contactCheckbox

    }
}