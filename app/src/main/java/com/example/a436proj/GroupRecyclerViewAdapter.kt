package com.example.a436proj

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.ExpandableGroupChildBinding
import com.example.a436proj.databinding.ExpandableGroupParentBinding

class GroupRecyclerViewAdapter(var context: Context, var groupModelList : MutableList<ExpandableGroupModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ExpandableGroupModel.PARENT -> {GroupParentViewHolder(ExpandableGroupParentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))}

            ExpandableGroupModel.CHILD -> {GroupChildViewHolder(ExpandableGroupChildBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))}

            else -> {GroupParentViewHolder(ExpandableGroupParentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))}
        }
    }

    override fun getItemCount(): Int = groupModelList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = groupModelList[position]
        when(row.type){
            ExpandableGroupModel.PARENT -> {
                (holder as GroupParentViewHolder).groupName.text = row.groupParent.groupName
                holder.arrow.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        holder.layout.setBackgroundColor(Color.WHITE)
                        collapseRow(position)


                    }else{
                        row.isExpanded = true
                        holder.layout.setBackgroundColor(Color.GRAY)
                        holder.arrow.setImageResource(R.mipmap.placeholder_image)
                        expandRow(position)
                    }
                }
                /*holder.upArrowImg.setOnClickListener{
                    if(row.isExpanded){
                        row.isExpanded = false
                        collapseRow(position)
                        holder.layout.setBackgroundColor(Color.WHITE)
                        holder.upArrowImg.visibility = View.GONE
                        holder.closeImage.visibility = View.VISIBLE

                    }
                }*/
            }


            ExpandableGroupModel.CHILD -> {
                (holder as GroupChildViewHolder).name.text = row.groupChild.name
                holder.timeSinceLastCall.text = row.groupChild.timeSinceLastCall
                holder.timeSinceLastText.text = row.groupChild.timeSinceLastText
            }
        }
    }

    override fun getItemViewType(position: Int): Int = groupModelList[position].type

    private fun expandRow(position: Int){
        val row = groupModelList[position]
        var nextPosition = position
        when (row.type) {
            ExpandableGroupModel.PARENT -> {
                for(child in row.groupParent.contacts){
                    groupModelList.add(++nextPosition, ExpandableGroupModel(ExpandableGroupModel.CHILD, child))
                }
                notifyDataSetChanged()
            }
            ExpandableGroupModel.CHILD -> {
                notifyDataSetChanged()
            }
        }
    }

    private fun collapseRow(position: Int){
        val row = groupModelList[position]
        var nextPosition = position + 1
        when (row.type) {
            ExpandableGroupModel.PARENT -> {
                outerloop@ while (true) {
                    //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")

                    if (nextPosition == groupModelList.size || groupModelList[nextPosition].type == ExpandableGroupModel.PARENT) {
                        /* println("Inside break $nextPosition and size is ${closedShelfModelList.size}")
                         closedShelfModelList[closedShelfModelList.size-1].isExpanded = false
                         println("Modified closedShelfModelList ${closedShelfModelList.size}")*/
                        break@outerloop
                    }

                    groupModelList.removeAt(nextPosition)
                }

                notifyDataSetChanged()
            }
        }
    }


    inner class GroupParentViewHolder(binding: ExpandableGroupParentBinding) : RecyclerView.ViewHolder(binding.root) {
        internal var layout = binding.expandableGroupContainer
        internal var groupName : TextView = binding.groupName
        internal var arrow = binding.arrow
    }

    inner class GroupChildViewHolder(binding : ExpandableGroupChildBinding) : RecyclerView.ViewHolder(binding.root) {
        internal var layout = binding.root
        internal var name = binding.name
        internal var timeSinceLastCall = binding.timeSinceLastCall
        internal var timeSinceLastText = binding.timeSinceLastText
    }
}