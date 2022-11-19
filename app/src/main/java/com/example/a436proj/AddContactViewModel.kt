package com.example.a436proj

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddContactViewModel: ViewModel(), DefaultLifecycleObserver {
    private val _contactsList = MutableLiveData<MutableList<ContactDto>>()

    internal val contactsList : MutableLiveData<MutableList<ContactDto>>
        get() = _contactsList

    private val _contactsListInitialized = MutableLiveData<Boolean>()

    internal val contactsListInitialized : MutableLiveData<Boolean>
        get() = _contactsListInitialized

    init {
        _contactsList.value  = mutableListOf()
        _contactsListInitialized.value = false
    }

    fun tickCheckBox(position : Int) {
        _contactsList.value!![position].isChecked = !_contactsList.value!![position].isChecked
    }
}