package com.example.a436proj

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalTime

class GroupActivity : AppCompatActivity() {

    var list = mutableListOf<ExpandableGroupModel>()
    private lateinit var googleAuth: GoogleSignInClient
    private lateinit var groupRV : GroupRecyclerViewAdapter
    private lateinit var viewModel : GroupViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityGroupBinding
    private val database = Firebase.database
    private val dbRef = database.getReference("contacts")
    private val reminderRef = database.getReference("Reminder")
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var notificationHandler: NotificationHandler

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NotificationHandler.LocalBinder
            notificationHandler = binder.getService()
            populateGroupIntervals()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val signInRequest = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("951802864601-aiqbs92cqaq3pljd2eei6apj1cpkmc6m.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleAuth = GoogleSignIn.getClient(this,signInRequest)
        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())
        binding = ActivityGroupBinding.inflate(layoutInflater)


        supportActionBar!!.title = "Groups"

        setContentView(binding.root)

        /*for (i in 1..4) {
            list.add(ExpandableGroupModel(ExpandableGroupModel.PARENT, SelectableGroups.Group(i.toString(),
                mutableListOf(
                    SelectableGroups.Group.Contact("Marcus Brooks",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890", false),
                    SelectableGroups.Group.Contact("Anthony Kim",  "TEST REMINDER TEXT", "(123)456-7890",false),
                    SelectableGroups.Group.Contact("Yun Chang",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890",false),
                    SelectableGroups.Group.Contact("Cheolhong Ahn",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890",false)
                ))))
        }*/

        viewModel = ViewModelProvider(this)[GroupViewModel::class.java]

        groupRV = GroupRecyclerViewAdapter(this, list, this).also {
            binding.list.adapter = it
            binding.list.setHasFixedSize(true)
        }

        viewModel.groups.observe(this) {
            groupRV.updateGroupModelList(viewModel.groups.value!!)
            Log.e("vieModel", "viweModel changed ${viewModel.groups.value!!}")

        }

        // setup notification channel and start handler
        creationNotificationChannel()
        Intent(this, NotificationHandler::class.java).also {intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        // once logged in, gets user info from database then update ui accordingly.
        //End of updating ui with database data.

        if (!viewModel.groupsInitialzed.value!!) {
            viewModel.groups.value = list
            updateUi()
            viewModel.groupsInitialzed.value = true
        }



        groupRV.settingsClickListener = GroupRecyclerViewAdapter.OnSettingsClickListener { model, position ->
            var groupSettingsIntent = Intent(this, GroupSettingsActivity::class.java)
            groupSettingsIntent.putExtra("groupName", model.groupParent.groupName)
            groupSettingsIntent.putExtra("contactsList",  (model.groupParent.contacts as Serializable))
            groupSettingsIntent.putExtra("groupIndex", position)
            startActivityForResult(groupSettingsIntent, 0)
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //For Chris: This if block is what gets run if the GroupSettingsActivity returns normally using the back button
                //We update the viewModel's groups list at the groupIndex that we get from the GroupSettingsActivity with the
                //new value of the contacts that we got from the GroupSettingsActivity. Then we update the RecyclerView
                var index = data?.extras?.get("groupIndex") as? Int
                var contacts = data?.extras?.get("resultContactsList") as? MutableList<SelectableGroups.Group.Contact>
                if (contacts != null && index != null) {
                    viewModel.groups.value!![index].groupParent.contacts = contacts
                    if (viewModel.groups.value!![index].isExpanded) {
                        groupRV.updateGroupModelList(viewModel.groups.value!!, shouldExpand = true, expandParentIndex = index)
                    }
                    else {
                        groupRV.updateGroupModelList(viewModel.groups.value!!)
                    }
                    firebaseAuth.currentUser?.let {
                        dbRef.child(it.uid).child(viewModel.groups.value!![index].groupParent.groupName).
                            setValue(viewModel.groups.value!![index].groupParent.contacts)
                    }
                }
            }

            if (resultCode == 1) {
                //For Chris: This block of code is what gets run if the group has been deleted.
                //Here we update the viewModel by removing the group in the groups list at the index received from
                //the GroupSettingsActivity. Then we update the RecyclerView
                var index = data?.extras?.get("groupIndex") as Int
                val name = viewModel.groups.value!![index].groupParent.groupName

                //deleting from database
                firebaseAuth.currentUser?.let {
                    dbRef.child(it.uid).child(name).removeValue()  //delete group contact
                    reminderRef.child(it.uid).child(name).removeValue() //delete alarm info.
                }

                viewModel.groups.value!!.removeAt(index)
                val shouldCollapse = index != viewModel.groups.value!!.size
                groupRV.updateGroupModelList(viewModel.groups.value!!, shouldCollapse, index)

                //For Chris: Connect the back end here. Update firebase with the new value of viewModel.groups.value!!
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id : Int = item.itemId

        if (id == R.id.add) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Add Group")

            var inputEditText = EditText(this)
            inputEditText.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(inputEditText)

            builder.setPositiveButton("Add Group") { dialog, which ->
                if(viewModel.groups != null){
                    Log.i("if duplicate check","xd")
                    for(i in 0 until (viewModel.groups.value!!.size!!)){
                        Log.i("group name", viewModel.groups.value!![i].groupParent.groupName)
                        Log.i("input name",inputEditText.text.toString())
                        if(viewModel.groups.value!![i].groupParent.groupName == inputEditText.text.toString()){
                            Log.i("duplicate found", "duplicate found")
                            Toast.makeText(
                                this,
                                "Group name already exists.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setPositiveButton
                        }
                    }
                }

                if(inputEditText.text.toString() == "") {
                    Toast.makeText(
                        this,
                        "Group name can't be empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    //For Chris: Here we create a new group within the add() call and update the viewModel's list
                    //of groups. Then we update the RecyclerView using groupRV.updateGroupModelList() with the
                    //new value of the viewModel.groups
                    viewModel.groups.value!!.add(ExpandableGroupModel(ExpandableGroupModel.PARENT,
                        SelectableGroups.Group(inputEditText.text.toString(),
                            mutableListOf<SelectableGroups.Group.Contact>())))

                    firebaseAuth.currentUser?.let {
                        dbRef.child(it.uid).child(inputEditText.text.toString()).setValue("empty")
                    }
                    groupRV.updateGroupModelList(viewModel.groups.value!!)

                    //For Chris: Connect the back end here. Update firebase with the new value of viewModel.groups.value!!
                }
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        if (id == R.id.sign_out) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Sign Out?")

            builder.setPositiveButton("Sign Out") { dialog, which ->
                pref = getSharedPreferences("Credentials", Context.MODE_PRIVATE) //shared ref
                with(pref.edit()){
                    putString("email", "")
                    putString("password", "")
                    apply()
                }
                FirebaseAuth.getInstance().signOut() // un-authorize current user out from firebase
                googleAuth.signOut()
                notificationHandler.cancelAllNotifications()

                finishAffinity()
                startActivity(Intent(this, AccessActivity::class.java))
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        return true
    }

    override fun onBackPressed() {
    }

    private fun populateGroupIntervals() {
        firebaseAuth.currentUser?.let {
            //get reminder info and set reminder
            reminderRef.child(it.uid).get().addOnCompleteListener(){task->
                if (!task.isSuccessful || task.result.value == null) {
                    Log.e("firebase", "Error getting data from reminder")
                    return@addOnCompleteListener
                }

                val result = task.result.value as Map<String, Map<String,Any>>
                result.forEach{(key,value)-> // each group name and its interval
                    var interval = Interval(IntervalType.Daily, LocalTime.of(0, 0))
                    value.forEach{(k,v)->
                        when(k) {
                            "intervalType" ->
                                if (v == IntervalType.Daily.printableName) {
                                    interval.intervalType = IntervalType.Daily
                                    interval.weeklyInterval =
                                        WeeklyInterval(DayOfWeek.MONDAY, 1)
                                } else if (v == IntervalType.Weekly.printableName) {
                                    interval.intervalType = IntervalType.Weekly
                                    reminderRef.child(it.uid).child(key).child("weeklyInterval")
                                        .get().addOnCompleteListener() { week ->
                                            val result = week.result.value as Map<String, Any>
                                            val day = result["day"]
                                            val weekInterval : Long = result["weekInterval"] as Long
                                            var dayOfWeek = DayOfWeek.MONDAY
                                            when (day) {
                                                "MONDAY" -> dayOfWeek = DayOfWeek.MONDAY
                                                "TUESDAY" -> dayOfWeek = DayOfWeek.TUESDAY
                                                "WEDNESDAY" -> dayOfWeek = DayOfWeek.WEDNESDAY
                                                "THURSDAY" -> dayOfWeek = DayOfWeek.THURSDAY
                                                "FRIDAY" -> dayOfWeek = DayOfWeek.FRIDAY
                                                "SATURDAY" -> dayOfWeek = DayOfWeek.SATURDAY
                                                "SUNDAY" -> dayOfWeek = DayOfWeek.SUNDAY
                                            }
                                            interval.weeklyInterval =
                                                WeeklyInterval(dayOfWeek, weekInterval.toInt())
                                        }
                                }
                            "timeToSendNotification" -> interval.timeToSendNotification =
                                LocalTime.of((v as Map<String,Int>)["hour"]!! ,v["minute"]!!, 0)
                        }
                    }
                    notificationHandler.setIntervalForGroup(key, interval)
                    notificationHandler.scheduleNotification(key, interval)
                }
            }
        }
    }

    private fun creationNotificationChannel() {
        val name = "Notification Channel"
        val desc = "notification channel for group interval"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateUi() {
            firebaseAuth.currentUser?.let {
                //retrieving group and contact info
                dbRef.child(it.uid).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        if (task.result.value != null) {
                            val result = task.result.value as Map<String, Any>
                            Log.i(
                                "firebase value",
                                "Got value ${result!!::class.java.typeName} in Group Activity"
                            )
                            Log.i("firebase result", "User is $result")
                            result.forEach { (key, v) ->
                                var contact: MutableList<SelectableGroups.Group.Contact> =
                                    mutableListOf()
                                if (v != "empty") {
                                    val value = v as MutableList<Map<String, Any>>
                                    for (i in 0 until value.size) {
                                        val new = SelectableGroups.Group.Contact("", "", "")
                                        value[i].forEach { (key, value) ->
                                            when (key) {
                                                "groupSettingsIsChecked" -> new.groupSettingsIsChecked =
                                                    value as Boolean
                                                "name" -> new.name = value as String
                                                "phoneNumber" -> new.phoneNumber = value as String
                                                "reminderText" -> new.reminderText = value as String
                                            }
                                        }
                                        contact.add(new)
                                    }
                                    viewModel.groups.value!!.add(
                                        ExpandableGroupModel(
                                            ExpandableGroupModel.PARENT,
                                            SelectableGroups.Group(
                                                key,
                                                contact
                                            )
                                        )
                                    )
                                }
                            }
                            groupRV.updateGroupModelList(viewModel.groups.value!!)
                        }
                    } else {
                        Log.e("firebase", "Error getting data from contacts")
                    }
                }
            }

    }

}