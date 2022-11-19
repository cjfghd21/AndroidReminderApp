package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupSettingsBinding

class GroupSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel : GroupSettingsViewModel
    lateinit var contactsLists : MutableList<SelectableGroups.Group.Contact>

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.getSerializableExtra("OurData").also { contactsLists = it as MutableList<SelectableGroups.Group.Contact> }
        Log.d("Our Activity Result", " getting here!!!!!!!!!!!!")
        Log.d("Our Activity Result", contactsLists.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityGroupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactsLists = intent.extras?.get("contactsList") as MutableList<SelectableGroups.Group.Contact>
        //val notificationsList = intent.extras

        /*val contactsList = mutableListOf(
            SelectableGroups.Group.Contact("Marcus Brooks", "0 days", "1 year", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Anthony Kim", "12 months", "30 seconds", "TEST REMINDER TEXT"),
            SelectableGroups.Group.Contact("Yun Chang", "3 weeks", "14 hours", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Cheolhong Ahn", "31 days", "10 minutes", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Marcus Brooks", "0 days", "1 year", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Anthony Kim", "12 months", "30 seconds", "TEST REMINDER TEXT"),
            SelectableGroups.Group.Contact("Yun Chang", "3 weeks", "14 hours", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Cheolhong Ahn", "31 days", "10 minutes", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Marcus Brooks", "0 days", "1 year", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Anthony Kim", "12 months", "30 seconds", "TEST REMINDER TEXT"),
            SelectableGroups.Group.Contact("Yun Chang", "3 weeks", "14 hours", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight."),
            SelectableGroups.Group.Contact("Cheolhong Ahn", "31 days", "10 minutes", "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.")
        )*/

        val notificationsList = mutableListOf(
            "Tell mom happy birthday.",
            "Buy a cake.",
            "Attend the group meeting at 8pm.",
            "JKHSADSAKJHSD AH SDKJA HDSKJAHKDJHAJKDH SAKHDKJ AKSDHKJA HDSKJAHD KSJAHDKJSAHKJDASH SDKHJSA DKJSAHD KJSAHDSKJA HDKSAJH DSKAJHD SKJAH DKSAJ HDSKAJHDSKJA HDSKJAHDSKJSA HDKSAJH DKSJA",
            "Tell mom happy birthday.",
            "Buy a cake.",
            "Attend the group meeting at 8pm.",
            "JKHSADSAKJHSD AH SDKJA HDSKJAHKDJHAJKDH SAKHDKJ AKSDHKJA HDSKJAHD KSJAHDKJSAHKJDASH SDKHJSA DKJSAHD KJSAHDSKJA HDKSAJH DSKAJHD SKJAH DKSAJ HDSKAJHDSKJA HDSKJAHDSKJSA HDKSAJH DKSJA",
            "Tell mom happy birthday.",
            "Buy a cake.",
            "Attend the group meeting at 8pm.",
            "JKHSADSAKJHSD AH SDKJA HDSKJAHKDJHAJKDH SAKHDKJ AKSDHKJA HDSKJAHD KSJAHDKJSAHKJDASH SDKHJSA DKJSAHD KJSAHDSKJA HDKSAJH DSKAJHD SKJAH DKSAJ HDSKAJHDSKJA HDSKJAHDSKJSA HDKSAJH DKSJA"
        )



        val notificationsRV = GroupSettingsNotificationsRecyclerViewAdapter(this, notificationsList).also {
            binding.notificationsRecyclerView.adapter = it
            binding.notificationsRecyclerView.setHasFixedSize(true)
        }

        val contactsRV = GroupSettingsContactRecyclerViewAdapter(this, contactsLists).also {
            binding.contactsRecyclerView.adapter = it
            binding.contactsRecyclerView.setHasFixedSize(true)
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
//            startActivity(intent)
            startActivityForResult(
                intent,
                1
            )

        }

        binding.deleteButton.setOnClickListener {

        }

        binding.checkAllCheckbox.setOnClickListener {
            if (binding.checkAllCheckbox.isChecked) {
                contactsRV.selectAll()
            }
            else {
                contactsRV.unselectAll()
            }
        }

        viewModel = ViewModelProvider(this)[GroupSettingsViewModel::class.java]

        viewModel.allTheSame.observe(this) {
            binding
        }

    }
}