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

    private val _sendSigninInformation = MutableLiveData<Boolean?>()
    val sendSigninInformation: LiveData<Boolean?>
        get() = _sendSigninInformation

    private val _navigateToTitle = MutableLiveData<Boolean?>()
    val navigateToTitle: LiveData<Boolean?>
        get() = _navigateToTitle

    private val _navigateToSignup = MutableLiveData<Boolean?>()
    val navigateToSignup: LiveData<Boolean?>
        get() = _navigateToSignup

    // For layout file. Trigger authentication
    fun onSendSigninInformation() {
        _sendSigninInformation.value = true
    }

    fun doneSendSigninInformation() {
        _sendSigninInformation.value = null
    }

    fun onNavigateToTitle() {
        _navigateToTitle.value = true
    }

    fun doneNavigateToTitle() {
        _navigateToTitle.value = null
    }

    fun onDoesntHaveAccount() {
        _navigateToSignup.value = true
    }

    fun doneDoesntHaveAccount() {
        _navigateToSignup.value = null
    }
}