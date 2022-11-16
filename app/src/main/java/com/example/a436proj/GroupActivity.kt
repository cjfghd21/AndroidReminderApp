package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.a436proj.databinding.ActivityGroupBinding
import java.io.Serializable

class GroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var list = mutableListOf<ExpandableGroupModel>()

        for (i in 1..4) {
            list.add(ExpandableGroupModel(ExpandableGroupModel.PARENT, SelectableGroups.Group(i.toString(),
                mutableListOf(
                    SelectableGroups.Group.Contact("Marcus Brooks", "0 days", "1 year", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
                    SelectableGroups.Group.Contact("Anthony Kim", "12 months", "30 seconds", "TEST REMINDER TEXT"),
                    SelectableGroups.Group.Contact("Yun Chang", "3 weeks", "14 hours", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
                    SelectableGroups.Group.Contact("Cheolhong Ahn", "31 days", "10 minutes", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.")
                ))))
        }

        var groupRV = GroupRecyclerViewAdapter(this, list).also {
            binding.list.adapter = it
            binding.list.setHasFixedSize(true)
        }

        groupRV.settingsClickListener = GroupRecyclerViewAdapter.OnSettingsClickListener {
            var groupSettingsIntent = Intent(this, GroupSettingsActivity::class.java)
            groupSettingsIntent.putExtra("contactsList",  (it.groupParent.contacts as Serializable))
            var result = startActivityForResult(groupSettingsIntent, 0)
        }
    }
}