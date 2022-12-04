package com.example.a436proj

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.a436proj.SelectableGroups.Group.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalTime


class FirebaseService: Service() {
    private val firebaseAuth : FirebaseAuth = requireNotNull(FirebaseAuth.getInstance())
    private val database = Firebase.database
    private val dbRef = database.getReference("contacts")
    private val nameRef = database.getReference("GroupNames")
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
            val pushKey = dbRef.child(it.uid).push().key
            nameRef.child(it.uid).child(groupName).setValue(pushKey)
            dbRef.child(it.uid).child(pushKey!!).updateChildren(mapOf("GroupName" to groupName ))
        }
    }

    fun setContacts(groupName: String, contacts: List<Contact>) {
        firebaseAuth.currentUser?.let {
            nameRef.child(it.uid).child(groupName).get().addOnCompleteListener(){task->
                val group = task.result.value.toString()
                dbRef.child(it.uid).child(group).setValue(contacts)
                dbRef.child(it.uid).child(group).updateChildren(mapOf("GroupName" to groupName))
            }


        }
    }


    fun setInterval(groupName: String, interval: Interval) {
        firebaseAuth.currentUser?.let {
            nameRef.child(it.uid).child(groupName).get().addOnCompleteListener() { task ->
                val group = task.result.value.toString()
                reminderRef.child(it.uid).child(group).setValue(interval)
                reminderRef.child(it.uid).child(group).updateChildren(mapOf("GroupName" to groupName))
            }
        }
    }

    fun deleteGroupInfo(groupName: String) {
        firebaseAuth.currentUser?.let {
            nameRef.child(it.uid).get().addOnCompleteListener(){str->
                val res = str.result.value as Map<String,String>
                Log.i("deleteGroup",groupName)
                val name = res[groupName].toString()
                Log.i("deleteName",name)
                nameRef.child(it.uid).child(groupName).removeValue()
                dbRef.child(it.uid).child(name).removeValue()
                reminderRef.child(it.uid).child(groupName).removeValue()
            }

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
                var result = task.result.value as MutableMap<String, Any>
                result = result.toSortedMap()

                Log.i("firebase value", "Got value ${result!!::class.java.typeName} in Group Activity")
                Log.i("firebase result", "User is $result")
                var groupNameToContacts = mutableMapOf<String, List<Contact>>()
                result.forEach{(key,v) ->
                    var contacts : MutableList<Contact> = mutableListOf()
                    val size = v as MutableMap<String,Any>
                    if((v as Map<String,Any>).size > 1){  //has more than just timestamp
                        size.remove("GroupName")
                        val temp = ArrayList<Any>(size.values)
                        Log.i("firebase size", "Got value ${size!!}")
                        Log.i("firebase size", "Got value ${size.size!!}")
                        for (i in 0 until size.size) {
                            val value = temp as MutableList<Map<String,Any>>
                            val new = Contact("", "", "")
                            (value[i] as Map<String,Any>).forEach { (key, value) ->
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
                Log.i("groupNameToContacts", "$groupNameToContacts")
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
                    var name :String =""
                    value.forEach{(k,v)->
                        when(k) {
                            "GroupName" -> name = v.toString()
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
                    groupNameToInterval[name] = interval
                    Log.i("notif group name", "$name")
                }
                callback(groupNameToInterval)
            }
        }
        return
    }
}