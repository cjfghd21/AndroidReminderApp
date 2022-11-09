package com.example.a436proj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a436proj.databinding.ActivityUitestingBinding

class UITestingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityUitestingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var list = mutableListOf<ExpandableGroupModel>()

        for (i in 1..4) {
            list.add(ExpandableGroupModel(ExpandableGroupModel.PARENT, SelectableGroups.Group(i.toString(), mutableListOf())))
            list.add(ExpandableGroupModel(ExpandableGroupModel.CHILD, SelectableGroups.Group.Contact("test", "0", "0")))
        }

        GroupRecyclerViewAdapter(
            this,
            list
        )
    }
}