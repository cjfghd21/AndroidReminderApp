package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.AddContactContactBinding

class ContactAdapter(items : List<ContactDto>, ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    private var list = items
    private var context = ctx
    private var checkedList = ArrayList<String>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.number.text = list[position].number

        if (holder.check_box.isChecked) {
            // store it in list
            checkedList.add(list[position].name)
        } else {
            checkedList.remove(list[position].name)
        }

    }
    fun getCheckedList(): ArrayList<String> {
        return checkedList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
        return ViewHolder(AddContactContactBinding.inflate( LayoutInflater.from(parent.context), parent, false))
    }

    fun filterList(filteredList: List<ContactDto>) {
        list = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(binding : AddContactContactBinding) : RecyclerView.ViewHolder(binding.root){
        val name : TextView = binding.name
        val number : TextView = binding.number

        var check_box = itemView.findViewById<CheckBox>(R.id.contact_checkbox)
    }
}