package com.example.a436proj

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupSettingsViewModel : ViewModel(), DefaultLifecycleObserver {

    private val _allTheSame = MutableLiveData<Boolean>()

    internal val allTheSame : LiveData<Boolean>
        get() = _allTheSame

}