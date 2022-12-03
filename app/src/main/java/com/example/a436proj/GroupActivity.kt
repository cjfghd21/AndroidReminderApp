package com.example.a436proj

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.sql.Array
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GroupActivity : AppCompatActivity() {

    var list = mutableListOf<ExpandableGroupModel>()
    private lateinit var googleAuth: GoogleSignInClient
    private lateinit var groupRV : GroupRecyclerViewAdapter
    private lateinit var viewModel : GroupViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityGroupBinding
    private var groupNameToInterval: MutableMap<String, Interval> = mutableMapOf()
    private val database = Firebase.database
    private val dbRef = database.getReference("contacts")
    private val reminderRef = database.getReference("Reminder")
    private lateinit var firebaseAuth: FirebaseAuth

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

        //once logged in, gets user info from database then update ui accordingly.
        firebaseAuth.currentUser?.let {
              viewModel.groups.value = list
              //retrieving group and contact info
              dbRef.child(it.uid).get().addOnCompleteListener(){ task->
                if(task.isSuccessful){
                    if(task.result.value != null) {
                        val result = task.result.value as Map<String, Any>
                        Log.i("firebase value", "Got value ${result!!::class.java.typeName} in Group Activity")
                        Log.i("firebase result", "User is $result")
                        result.forEach{(key,v) ->
                            var contact : MutableList<SelectableGroups.Group.Contact> = mutableListOf()
                            if(v != "empty"){
                                val value = v as MutableList<Map<String,Any>>
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
                            }
                            viewModel.groups.value!!.add(ExpandableGroupModel(ExpandableGroupModel.PARENT,
                                SelectableGroups.Group(key,
                                    contact)))
                            groupRV.updateGroupModelList(viewModel.groups.value!!)
                        }
                    }
                }else{
                    Log.e("firebase", "Error getting data from contacts")
                }
            }
            //get reminder info and set reminder
            reminderRef.child(it.uid).get().addOnCompleteListener(){task->
                if(task.isSuccessful){
                    if(task.result.value != null) {
                        val result = task.result.value as Map<String, Map<String,Any>>
                        result.forEach{(key,value)->                        //each group names and its interval
                            var intervals: Interval = Interval(IntervalType.Daily, LocalTime.of(0, 0))
                            value.forEach{(k,v)->
                                when(k) {
                                    "intervalType" ->
                                        if (v == "Daily") {
                                            intervals.intervalType = IntervalType.Daily
                                            intervals.weeklyInterval =
                                                WeeklyInterval(DayOfWeek.MONDAY, 1)
                                        } else if (v == "Weekly") {
                                            intervals.intervalType = IntervalType.Weekly
                                            reminderRef.child(it.uid).child(key).child("weeklyInterval")
                                                .get().addOnCompleteListener() { week ->
                                                val result = task.result.value as Map<String, Any>
                                                val day = result["day"]
                                                val weekInterval: Int = result["weekInterval"] as Int
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
                                                intervals.weeklyInterval =
                                                    WeeklyInterval(dayOfWeek, weekInterval)
                                            }
                                        }
                                    "lastUpdateTimeStamp" -> intervals.lastUpdateTimestamp =
                                        LocalDateTime.now()
                                    "timeToSendNotification" -> intervals.timeToSendNotification =
                                        LocalTime.of((v as Map<String,Int>)["hour"]!! ,v["minute"]!!, 0)
                                }
                            }
                            groupNameToInterval[key] = intervals
                            scheduleNotification(key, intervals)
                        }


                    }
                    else{
                        Log.e("firebase", "Error getting data from reminder")
                    }
                }
            }
        }

        if (!viewModel.groupsInitialzed.value!!) {
            viewModel.groups.value = list
            viewModel.groupsInitialzed.value = true
        }



        groupRV.settingsClickListener = GroupRecyclerViewAdapter.OnSettingsClickListener { model, position ->
            var groupSettingsIntent = Intent(this, GroupSettingsActivity::class.java)
            groupSettingsIntent.putExtra("groupName", model.groupParent.groupName)
            groupSettingsIntent.putExtra("contactsList",  (model.groupParent.contacts as Serializable))
            groupSettingsIntent.putExtra("groupIndex", position)
            startActivityForResult(groupSettingsIntent, 0)
        }



        // setup notification channel
        creationNotificationChannel()

        // fetch related Intervals from firebase for each Group and fill "groupIdToInterval"
        // then schedule notification
    }



    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //For Chris: This if block is what gets run if the GroupSettingsActivity returns normally using the back button
                //We update the viewModel's groups list at the groupIndex that we get from the GroupSettingsActivity with the
                //new value of the contacts that we got from the GroupSettingsActivity. Then we update the RecyclerView
                var index = data?.extras?.get("groupIndex") as Int
                var groupName = viewModel.groups.value!![index].groupParent.groupName
                var contacts = data?.extras?.get("resultContactsList") as? MutableList<SelectableGroups.Group.Contact>
                if (contacts != null) {
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

                var interval = data?.extras?.get("interval") as? Interval
                if (interval != null) {


                    //storing reminder to group
                    firebaseAuth.currentUser?.let {
                        reminderRef.child(it.uid).child(groupName).setValue(interval)
                    }
                    groupNameToInterval[groupName] = interval
                    scheduleNotification(groupName, interval)
                    showAlert(interval) //moved here so when it recreates doesnt show same message.
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
                groupRV.updateGroupModelList(viewModel.groups.value!!, true, index)

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
                FirebaseAuth.getInstance().signOut() //unauthorize current user out from firebase
                googleAuth.signOut()
                //to Yun: delete notification set here.

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

    private fun creationNotificationChannel() {
        val name = "Notification Channel"
        val desc = "description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(groupName: String, interval: Interval) {
        val intent = Intent(applicationContext, Notification::class.java)
        // set action with groupName so that intent's filterEquals is true for the same group.
        intent.setAction(groupName)
        intent.putExtra(content, String.format("Send notification to group %s", groupName))
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // cancel if previously tied intent exists
        alarmManager.cancel(pendingIntent)

        // set new alarm
        val time = getTime(interval)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getTime(interval: Interval): Long {
        val minute = interval.timeToSendNotification.minute
        val hour = interval.timeToSendNotification.hour
        val calendar = getCalendar(interval)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        return calendar.timeInMillis
    }

//    private fun getInterval(interval: Interval): Long {
//        when(interval.intervalType) {
//            IntervalType.Daily -> {
//                return AlarmManager.INTERVAL_DAY // 24 hours in milliseconds
//            }
//            IntervalType.Weekly -> {
//                // one week * weekInterval in milliseconds
//                return interval.weeklyInterval.weekInterval.toLong() * AlarmManager.INTERVAL_DAY * 7
//            }
//        }
//    }

    private fun getCalendar(interval: Interval): Calendar {
        var current = LocalDateTime.now()

        when(interval.intervalType) {
            IntervalType.Daily -> {
                if (current.toLocalTime().isAfter(interval.timeToSendNotification)) {
                    current = current.plusDays(1)
                }
            }
            IntervalType.Weekly -> {
                if (current.dayOfWeek > interval.weeklyInterval.day) {
                    current = current.plusDays((interval.weeklyInterval.day.value - current.dayOfWeek.value + 7).toLong())
                } else if (current.dayOfWeek < interval.weeklyInterval.day) {
                    current = current.plusDays((interval.weeklyInterval.day.value - current.dayOfWeek.value).toLong())
                } else {
                    if (current.toLocalTime().isAfter(interval.timeToSendNotification)) {
                        current = current.plusDays(7)
                    }
                }
            }
        }

        val date = current.dayOfMonth
        val month = current.monthValue
        val year = current.year
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, date)
        return calendar
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
            IntervalType.Weekly-> String.format("Weekly Notification is scheduled at %s %s", interval.weeklyInterval.day.name, time)
        }
    }

}