package com.example.agentd.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val _navigateToTitle = MutableLiveData<Boolean?>()
    val navigateToTitle: LiveData<Boolean?>
        get() = _navigateToTitle

    private val _signout = MutableLiveData<Boolean?>()
    val signout: LiveData<Boolean?>
        get() = _signout

    fun onBackToTitle() {
        _navigateToTitle.value = true
    }

    fun doneBackToTitle() {
        _navigateToTitle.value = false
    }

    fun onSignout() {
        _signout.value = true
    }

    fun doneSignout() {
        _signout.value = false
    }
}