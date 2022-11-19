package com.example.a436proj

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupSettingsViewModel : ViewModel(), DefaultLifecycleObserver {

    private val _allSelected = MutableLiveData<Boolean>()
    private val _contactsList = MutableLiveData<MutableList<SelectableGroups.Group.Contact>>()

    internal val allSelected : LiveData<Boolean>
        get() = _allSelected

    internal val contactsList : MutableLiveData<MutableList<SelectableGroups.Group.Contact>>
        get() = _contactsList

    private val _contactsListInitialized = MutableLiveData<Boolean>()

    internal val contactsListInitialzed : MutableLiveData<Boolean>
        get() = _contactsListInitialized


    init {
        _allSelected.value = false
        _contactsList.value = mutableListOf()
        _contactsListInitialized.value = false
    }

    fun selectAll() {
        for (i in 0 until _contactsList.value!!.size) {
            _contactsList.value!![i].groupSettingsIsChecked = true
        }
    }

    fun unselectAll() {
        for (i in 0 until _contactsList.value!!.size) {
            _contactsList.value!![i].groupSettingsIsChecked = false
        }
    }

    fun deleteChecked() {
        _contactsList.value!!.removeAll {
            it.groupSettingsIsChecked
        }
        _allSelected.value = false
    }

    fun tickCheckBox(position : Int) {
        var checkedFound = false
        var notCheckedFound = false

        _contactsList.value!![position].groupSettingsIsChecked = !_contactsList.value!![position].groupSettingsIsChecked

        for (i in 0 until _contactsList.value!!.size) {
            if (_contactsList.value!![i].groupSettingsIsChecked) {
                checkedFound = true
            } else {
                notCheckedFound = true
            }
        }

        if (!checkedFound.xor(notCheckedFound)) {
            _allSelected.value = false
        }
        else {
            _allSelected.value = checkedFound
        }
    }
}