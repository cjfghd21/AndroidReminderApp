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
import com.example.a436proj.SelectableGroups.Group.Contact
import com.example.a436proj.databinding.ActivityGroupSettingsBinding
import java.io.Serializable
import java.time.format.DateTimeFormatter

class GroupSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel : GroupSettingsViewModel
    lateinit var contactsLists : MutableList<Contact>
    private lateinit var contactsRV : GroupSettingsContactRecyclerViewAdapter
    private var groupIndex : Int = -1
    private var groupName : String = ""
    private var resultIntent : Intent? = Intent()

    private lateinit var firebaseService: FirebaseService
    private lateinit var notificationHandler: NotificationHandler

    private val notificationConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NotificationHandler.LocalBinder
            notificationHandler = binder.getService()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    private val firebaseConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FirebaseService.LocalBinder
            firebaseService = binder.getService()
            // bind notification handler after connecting firebase
            bindNotificationHandler()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    private fun bindNotificationHandler() {
        Intent(this, NotificationHandler::class.java).also {intent ->
            bindService(intent, notificationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityGroupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "Group Settings"

        if (savedInstanceState != null) {
            resultIntent = savedInstanceState.getParcelable("resultIntent")
            setResult(RESULT_OK, resultIntent)
        }

        // setup firebase connection
        Intent(this, FirebaseService::class.java).also {intent ->
            bindService(intent, firebaseConnection, Context.BIND_AUTO_CREATE)
        }

        val contactsList : MutableList<Contact> = intent.extras?.get("contactsList") as MutableList<Contact>
        groupIndex = intent.getIntExtra("groupIndex", 0)
        groupName = intent.getStringExtra("groupName")!!

        viewModel = ViewModelProvider(this)[GroupSettingsViewModel::class.java]

        if (!viewModel.contactsListInitialzed.value!!) {
            Log.d("view model", "ViewModel Initialized")
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
            resultIntent!!.putExtra("resultContactsList", viewModel.contactsList.value!! as Serializable)
            resultIntent!!.putExtra("groupIndex", groupIndex)
            setResult(RESULT_OK, resultIntent)

            //For Chris: Connect back end here. Update firebase with the updated viewModel.ContactsList.value!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (resultIntent != null) {
            outState.putParcelable("resultIntent", resultIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id : Int = item.itemId

        if (id == R.id.delete_group_button) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Group: ${intent.extras?.get("groupName")}?")

            builder.setPositiveButton("Delete") { dialog, which ->
                val deleteIntent = Intent()
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
            val notificationIntent = Intent(this, NotificationsActivity::class.java)
            startActivityForResult(notificationIntent, NotificationsActivity.requestCode)
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == 1) {
                val resultList = data?.extras?.get("newContactsList") as MutableList<Contact>
                for (i in 0 until resultList.size) {
                    viewModel.contactsList.value!!.add(resultList[i])
                }
                Log.i("contacts value","viewModel is ${viewModel.contactsList.value!!::class.java.typeName}")

                contactsRV.updateContactsList(viewModel.contactsList.value!!)
                resultIntent!!.putExtra("resultContactsList", viewModel.contactsList.value!! as Serializable)
                resultIntent!!.putExtra("groupIndex", groupIndex)
                setResult(RESULT_OK, resultIntent)

                //For Chris: Connect back end here. Update firebase with the updated viewModel.ContactsList.value!!
            }
        } else if (requestCode == NotificationsActivity.requestCode) {
            if (resultCode == RESULT_OK) {
                var interval = data!!.getSerializableExtra("interval") as Interval
                firebaseService.setInterval(groupName, interval)
                notificationHandler.scheduleNotification(groupName, interval)
                showAlert(interval)
            }
            return
        }
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