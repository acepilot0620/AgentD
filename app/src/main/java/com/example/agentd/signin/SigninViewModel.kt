package com.example.agentd.signin

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SigninViewModel : ViewModel() {

    private val _sendSignInformation = MutableLiveData<Boolean?>()
    val sendSignInformation: LiveData<Boolean?>
        get() = _sendSignInformation

    private val _navigateToSignup = MutableLiveData<Boolean?>()
    val navigateToSignup: LiveData<Boolean?>
        get() = _navigateToSignup

    // For layout file. Trigger authentication
    fun onSignin() {
        _sendSignInformation.value = true
    }

    fun doneSignin() {
        _sendSignInformation.value = null
    }

    fun onDoesntHaveAccount() {
        _navigateToSignup.value = true
    }

    fun doneDoesntHaveAccount() {
        _navigateToSignup.value = false
    }
}