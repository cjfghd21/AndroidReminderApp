package com.example.a436proj

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.a436proj.SelectableGroups.Group.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalTime

class FirebaseService: Service() {
    private val firebaseAuth : FirebaseAuth = requireNotNull(FirebaseAuth.getInstance())
    private val database = Firebase.database
    private val dbRef = database.getReference("contacts")
    private val reminderRef = database.getReference("Reminder")

    inner class LocalBinder: Binder() {
        fun getService(): FirebaseService = this@FirebaseService
    }
    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun setEmptyContacts(groupName: String) {
        firebaseAuth.currentUser?.let {
            dbRef.child(it.uid).child(groupName).setValue("empty")
        }
    }

    fun setContacts(groupName: String, contacts: List<Contact>) {
        firebaseAuth.currentUser?.let {
            dbRef.child(it.uid).child(groupName).setValue(contacts)
        }
    }

    fun setInterval(groupName: String, interval: Interval) {
        firebaseAuth.currentUser?.let {
            reminderRef.child(it.uid).child(groupName).setValue(interval)
        }
    }

    fun deleteGroupInfo(groupName: String) {
        firebaseAuth.currentUser?.let {
            dbRef.child(it.uid).child(groupName).removeValue()
            reminderRef.child(it.uid).child(groupName).removeValue()
        }
    }

    fun getGroupNameToContacts(callback: (groupNameToContacts: Map<String, List<Contact>>) -> Unit) {
        firebaseAuth.currentUser?.let {
            // retrieving group and contact info
            dbRef.child(it.uid).get().addOnCompleteListener(){ task->
                if (!task.isSuccessful || task.result.value == null) {
                    Log.e("firebase", "Error getting data from reminder")
                    return@addOnCompleteListener
                }
                val result = task.result.value as Map<String, Any>
                Log.i("firebase value", "Got value ${result!!::class.java.typeName} in Group Activity")
                Log.i("firebase result", "User is $result")
                var groupNameToContacts = mutableMapOf<String, List<Contact>>()
                result.forEach{(key,v) ->
                    var contacts : MutableList<Contact> = mutableListOf()
                    if(v != "empty"){
                        val value = v as MutableList<Map<String,Any>>
                        for (i in 0 until value.size) {
                            val new = Contact("", "", "")
                            value[i].forEach { (key, value) ->
                                when (key) {
                                    "groupSettingsIsChecked" -> new.groupSettingsIsChecked =
                                        value as Boolean
                                    "name" -> new.name = value as String
                                    "phoneNumber" -> new.phoneNumber = value as String
                                    "reminderText" -> new.reminderText = value as String
                                }
                            }
                            contacts.add(new)
                        }
                    }
                    groupNameToContacts[key] = contacts
                }
                callback(groupNameToContacts)
            }
        }
        return
    }

    fun getGroupNameToInterval(callback: (groupNameToInterval: Map<String, Interval>) -> Unit) {
        firebaseAuth.currentUser?.let {
            //get reminder info and set reminder
            reminderRef.child(it.uid).get().addOnCompleteListener(){task->
                if (!task.isSuccessful || task.result.value == null) {
                    Log.e("firebase", "Error getting data from reminder")
                    return@addOnCompleteListener
                }

                val result = task.result.value as Map<String, Map<String,Any>>
                var groupNameToInterval = mutableMapOf<String, Interval>()
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
                    groupNameToInterval.put(key, interval)
                }
                callback(groupNameToInterval)
            }
        }
        return
    }
}