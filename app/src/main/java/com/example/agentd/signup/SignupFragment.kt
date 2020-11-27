package com.example.agentd.signup

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.data.User
import com.example.agentd.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSignupBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false)


        val viewModelFactory = SignupViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val signupViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SignupViewModel::class.java)

        binding.signupViewModel = signupViewModel

        binding.setLifecycleOwner(this)

        signupViewModel.sendSignupInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val username: String = binding.signupInputUsername.text.toString()
                val email: String = binding.signupInputEmail.text.toString()
                val password: String = binding.signupInputPassword.text.toString()
                val phoneNumber: String = binding.signupInputPhoneNumber.text.toString()

                var success: Boolean = performSignup(username, email, password, phoneNumber)
                if(!success) return@Observer

                signupViewModel.doneSignup()
            }
        })

        signupViewModel.navigateToSignin.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d("SignupFragment", "Try to show SigninFragment")

                this.findNavController().navigate(
                    SignupFragmentDirections
                        .actionSignupFragmentToSigninFragment()
                )

                signupViewModel.doneAlreadyHaveAccount()
            }
        })

        return binding.root
    }

    private fun performSignup(username: String, email: String, password: String, phoneNumber: String): Boolean {
        // if empty string on authentication, app just crashed. To deal with that,
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return false
        }

        Log.d("SignupFragment", "Username is: " + username)
        Log.d("SignupFragment", "email is: " + email)
        Log.d("SignupFragment", "password is: " + password)
        Log.d("SignupFragment", "phonenumber is: " + phoneNumber)

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(!task.isSuccessful) return@addOnCompleteListener

                // else if successfull
                Log.d("SignupFragment", "Successfully created user with uid: ${task.result?.user?.uid}")

                saveUserToFirebaseDatabase(task.result?.user?.uid.toString(), username, email, phoneNumber)
            }
            .addOnFailureListener { task ->
                Toast.makeText(requireActivity(), task.message, Toast.LENGTH_SHORT).show()
                Log.d("SigninFragment", "Failed to create user: ${task.message}")
            }
        return true
    }

//    fun authenticateFirebase(email: String, password: String): String? {
//        return (activity as MainActivity).authenticateFirebase(email, password)
//    }

    private fun saveUserToFirebaseDatabase(uid: String, username: String, email: String, phoneNumber: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid") // "users" node
        val user = User(uid, username, email, phoneNumber)

//        ref.setValue()

    }

}