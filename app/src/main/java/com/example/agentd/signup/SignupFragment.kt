package com.example.agentd.signup

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

                performSignup(username, email, password, phoneNumber)

                signupViewModel.doneSignup()

                // immediately return to signin fragment
                signupViewModel.onAlreadyHaveAccount()
            }
        })

        signupViewModel.navigateToSignin.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show SigninFragment")

                this.findNavController().navigate(
                    SignupFragmentDirections
                        .actionSignupFragmentToSigninFragment()
                )

                signupViewModel.doneAlreadyHaveAccount()
            }
        })

        return binding.root
    }

    private fun performSignup(username: String, email: String, password: String, phoneNumber: String) {
        // if empty string on authentication, app just crashed. To deal with that,
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please enter text in email/password", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "Username is: " + username)
        Log.d(TAG, "email is: " + email)
        Log.d(TAG, "password is: " + password)
        Log.d(TAG, "phonenumber is: " + phoneNumber)

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(!task.isSuccessful) return@addOnCompleteListener

                val uid: String = task.result?.user?.uid.toString()

                // else if successfull
                Log.d(TAG, "Successfully created user with uid: ${uid}")

                saveUserToFirebaseDatabase(uid, username, email, phoneNumber)
            }
            .addOnFailureListener { task ->
                Toast.makeText(requireActivity(), task.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failed to create user: ${task.message}")
            }
    }


    private fun saveUserToFirebaseDatabase(uid: String, username: String, email: String, phoneNumber: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid") // "users" node
        val user = User(uid, username, email, phoneNumber,
            0, // initial balance 0 won
            37.5642135, 127.0016985 // initial location as the center of Seoul city
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved user data")
            }
            .addOnFailureListener {
                // for logging purposes
            }

    }

    companion object {
        val TAG = "Signup"
    }

}