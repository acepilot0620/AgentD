package com.example.agentd.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignupViewModel : ViewModel() {

    private val _sendSignupInformation = MutableLiveData<Boolean?>()
    val sendSignupInformation: LiveData<Boolean?>
        get() = _sendSignupInformation


    private val _navigateToSignin = MutableLiveData<Boolean?>()
    val navigateToSignin: LiveData<Boolean?>
        get() = _navigateToSignin


    // For layout file. Trigger authentication
    fun onSignup() {
        _sendSignupInformation.value = true
    }

    fun doneSignup() {
        _sendSignupInformation.value = null
    }

    fun onAlreadyHaveAccount() {
        _navigateToSignin.value = true
    }

    fun doneAlreadyHaveAccount() {
        _navigateToSignin.value = false
    }
}