package com.example.agentd.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agentd.signup.SignupViewModel

class SigninViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SigninViewModel::class.java)) {
            return SigninViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}