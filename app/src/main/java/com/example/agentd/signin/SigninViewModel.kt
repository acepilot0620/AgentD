package com.example.agentd.signin

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.agentd.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SigninViewModel : ViewModel() {

    var googleSigninClient : GoogleSignInClient? = null

    fun loginGoogle(email: String, password: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()

        // googleSigninClient = GoogleSignIn.getClient()
    }
}