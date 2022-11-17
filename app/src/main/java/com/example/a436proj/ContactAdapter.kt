package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.AddContactContactBinding

class ContactAdapter(items : List<ContactDto>, ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    private var list = items
    private var context = ctx

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.number.text = list[position].number

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

    }
}