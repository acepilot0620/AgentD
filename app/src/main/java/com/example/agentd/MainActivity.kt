package com.example.agentd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
//    var googleSigninClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val toolbar = findViewById<Toolbar>(R.id.top_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        val host: NavHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
//
//        val navController = host.navController


    }

//    fun loginGoogle(email: String, password: String) {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(R.string.default_web_client_id.toString())
//            .requestEmail()
//            .build()
//
//         googleSigninClient = GoogleSignIn.getClient(this, gso)
//    }

//    fun authenticateFirebase(email: String, password: String): String? {
//        var endMessage: String? = null
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//            if(task.isSuccessful) {
//                endMessage = "Signup Complete!"
//            } else {
//                endMessage =  "Signup Failed.."
//            }
//
//        }
//        return endMessage
//    }
}