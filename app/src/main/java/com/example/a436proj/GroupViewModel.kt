package com.example.a436proj

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupViewModel : ViewModel(), DefaultLifecycleObserver {

    private val _groups = MutableLiveData<MutableList<ExpandableGroupModel>>()

    internal val groups : MutableLiveData<MutableList<ExpandableGroupModel>>
        get() = _groups

    private val _groupsInitialized = MutableLiveData<Boolean>()

    internal val groupsInitialzed : MutableLiveData<Boolean>
            get() = _groupsInitialized

    init {
        _groupsInitialized.value = false
    }

}