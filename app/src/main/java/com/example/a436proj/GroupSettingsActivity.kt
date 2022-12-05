package com.example.a436proj

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.time.format.DateTimeFormatter

class GroupSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel : GroupSettingsViewModel
    lateinit var contactsLists : MutableList<SelectableGroups.Group.Contact>
    private lateinit var contactsRV : GroupSettingsContactRecyclerViewAdapter
    private var groupIndex : Int = -1
    private var groupName: String = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private val database = Firebase.database
    private val reminderRef = database.getReference("Reminder")
    private lateinit var notificationHandler: NotificationHandler

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NotificationHandler.LocalBinder
            notificationHandler = binder.getService()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == 1) {
                //For Chris: resultList is the contacts that are being added to the group
                var resultList = data?.extras?.get("newContactsList") as MutableList<SelectableGroups.Group.Contact>
                for (i in 0 until resultList.size) {
                    viewModel.contactsList.value!!.add(resultList[i])
                }
                Log.i("contacts value","viewModel is ${viewModel.contactsList.value!!!!::class.java.typeName}")

                contactsRV.updateContactsList(viewModel.contactsList.value!!)
                viewModel.resultIntent.value!!.putExtra("resultContactsList", viewModel.contactsList.value!! as Serializable)
                viewModel.resultIntent.value!!.putExtra("groupIndex", groupIndex)
                //For Chris: Connect back end here. Update firebase with the updated viewModel.ContactsList.value!!
            }
        } else if (requestCode == NotificationsActivity.requestCode) {
            if (resultCode == RESULT_OK) {
                var interval = data!!.getSerializableExtra("interval") as Interval
                //storing reminder to group
                firebaseAuth.currentUser?.let {
                    reminderRef.child(it.uid).child(groupName).setValue(interval)
                }
                notificationHandler.setIntervalForGroup(groupName, interval)
                notificationHandler.scheduleNotification(groupName, interval)
                showAlert(interval)
            }
            return
        }

       /* data?.getSerializableExtra("OurData").also { contactsLists = it as MutableList<SelectableGroups.Group.Contact> }
        Log.d("Our Activity Result", " getting here!!!!!!!!!!!!")
        Log.d("Our Activity Result", contactsLists.toString())*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityGroupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "Group Settings"

        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())

        val contactsList : MutableList<SelectableGroups.Group.Contact> = intent.extras?.get("contactsList") as MutableList<SelectableGroups.Group.Contact>
        groupIndex = intent.extras?.get("groupIndex") as Int
        groupName = intent.getStringExtra("groupName")!!

        Intent(this, NotificationHandler::class.java).also {intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

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

        viewModel = ViewModelProvider(this)[GroupSettingsViewModel::class.java]

        if (!viewModel.contactsListInitialzed.value!!) {
            Log.d("sajhdjskadsa", "ViewModel Initialized")
            viewModel.contactsList.value = contactsList
            viewModel.contactsListInitialzed.value = true
        }

        //viewModel.contactsList.value = contactsList

        viewModel.allSelected.observe(this) {
            binding.checkAllCheckbox.isChecked = it
        }

//        val notificationsRV = GroupSettingsNotificationsRecyclerViewAdapter(this, notificationsList).also {
//            binding.notificationsRecyclerView.adapter = it
//            binding.notificationsRecyclerView.setHasFixedSize(true)
//        }

        contactsRV = GroupSettingsContactRecyclerViewAdapter(this, viewModel.contactsList.value!!, viewModel::tickCheckBox).also {
            binding.contactsRecyclerView.adapter = it
            binding.contactsRecyclerView.setHasFixedSize(true)
        }

        binding.checkAllCheckbox.setOnClickListener {
            if (binding.checkAllCheckbox.isChecked) {
                viewModel.selectAll()
                contactsRV.updateContactsList(viewModel.contactsList.value!!)
            }
            else {
                viewModel.unselectAll()
                contactsRV.updateContactsList(viewModel.contactsList.value!!)
            }
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            intent.putExtra("currentContacts", viewModel.contactsList.value!! as Serializable)
//            startActivity(intent)
            startActivityForResult(intent, 0)
        }

        binding.deleteButton.setOnClickListener {
            //For Chris: This button deletes from the viewModel all of the contacts that have the value groupSettingsChecked = true
            //Then we update the RecyclerView. After we do that, we create a new Intent and add the resulting contacts list
            //to the intent, as well as the groupIndex that was passed to this activity. Then we set the result using the
            //result code RESULT_OK and the intent we created
            viewModel.deleteChecked()
            contactsRV.updateContactsList(viewModel.contactsList.value!!)
            viewModel.resultIntent.value!!.putExtra("resultContactsList", viewModel.contactsList.value!! as Serializable)
            viewModel.resultIntent.value!!.putExtra("groupIndex", groupIndex)

            //For Chris: Connect back end here. Update firebase with the updated viewModel.ContactsList.value!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id : Int = item.itemId

        if (id == R.id.delete_group_button) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Group: ${intent.extras?.get("groupName")}?")

            builder.setPositiveButton("Delete") { dialog, which ->
                var deleteIntent = Intent()
                deleteIntent.putExtra("groupIndex", groupIndex)
                setResult(1, deleteIntent)
                finish()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        if (id == R.id.notification_settings_button) {
            var notificationIntent = Intent(this, NotificationsActivity::class.java)
            startActivityForResult(notificationIntent, NotificationsActivity.requestCode)
        }

        return true
    }

    override fun onBackPressed() {
        setResult(RESULT_OK,viewModel.resultIntent.value!!)
        super.onBackPressed()
    }

    private fun showAlert(interval: Interval) {
        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(getAlertMessage(interval))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getAlertMessage(interval: Interval): String {
        val time = interval.timeToSendNotification.format(DateTimeFormatter.ISO_TIME)
        return when(interval.intervalType){
            IntervalType.Daily -> String.format("Daily Notification is scheduled at %s", time)
            IntervalType.Weekly-> when(interval.weeklyInterval.weekInterval){
                1 -> String.format("Weekly Notification is scheduled at %s %s", interval.weeklyInterval.day.name, time)
                2 -> String.format("Biweekly Notification is scheduled at %s %s", interval.weeklyInterval.day.name, time)
                3 -> String.format("Triweekly Notification is scheduled at %s %s", interval.weeklyInterval.day.name, time)
                4 -> String.format("Quatriweekly Notification is scheduled at %s %s", interval.weeklyInterval.day.name, time)
                else -> throw Exception("Invalid weekly interval value provided!")
            }
        }
    }
}