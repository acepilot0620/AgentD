package com.example.agentd.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupViewModel : ViewModel() {

    private val _sendInformation = MutableLiveData<Boolean?>()
    val sendInformation: LiveData<Boolean?>
        get() = _sendInformation

    // For layout file. Trigger authentication
    fun onSignup() {
        _sendInformation.value = true
    }

    fun authenticateFirebase(email: String, password: String): String? {
        var endMessage: String? = null
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                endMessage = "Signup Complete!"
            } else {
                endMessage =  "Signup Failed.."
            }

        }
        return endMessage
    }

    fun doneSignup() {
        _sendInformation.value = null
    }
}