package com.example.agentd.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val _navigateToSignup = MutableLiveData<Boolean?>()
    val navigateToSignup: LiveData<Boolean?>
        get() = _navigateToSignup
}