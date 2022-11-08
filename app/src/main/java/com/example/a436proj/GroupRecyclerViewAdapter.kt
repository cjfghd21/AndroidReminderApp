package com.example.a436proj

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GroupRecyclerViewAdapter(var context: Context, var groupModelList : MutableList<ExpandableGroupModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ExpandableGroupModel.PARENT -> {GroupParentViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.expandable_group_parent, parent, false))}

            ExpandableGroupModel.CHILD -> { GroupChildViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.expandable_group_child, parent, false))  }

            else -> {GroupParentViewHolder(
                LayoutInflater.from(parent.context).inflate(
            R.layout.expandable_group_parent, parent, false))}
        }
    }

    override fun getItemCount(): Int = groupModelList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = groupModelList[position]
        when(row.type){
            ExpandableGroupModel.PARENT -> {
                (holder as GroupParentViewHolder).countryName.text = row.countryParent.country
                holder.closeImage.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        collapseRow(position)
                        holder.layout.setBackgroundColor(Color.WHITE)


                    }else{
                        holder.layout.setBackgroundColor(Color.GRAY)
                        row.isExpanded = true
                        holder.upArrowImg.visibility = View.VISIBLE
                        holder.closeImage.visibility = View.GONE
                        expandRow(position)
                    }
                }
                holder.upArrowImg.setOnClickListener{
                    if(row.isExpanded){
                        row.isExpanded = false
                        collapseRow(position)
                        holder.layout.setBackgroundColor(Color.WHITE)
                        holder.upArrowImg.visibility = View.GONE
                        holder.closeImage.visibility = View.VISIBLE

                    }
                }
            }


            ExpandableCountryModel.CHILD -> {
                (holder as CountryStateChildViewHolder).stateName.text = row.countryChild.name
                holder.capitalImage.text = row.countryChild.capital
            }
        }

    }


    inner class GroupParentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        internal var layout = itemView.expandable_group_container
        internal var groupName : TextView = itemView.group_name
    }

    inner class GroupChildViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        internal var layout = itemView
    }
}