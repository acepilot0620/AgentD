package com.example.agentd.signin

import android.location.Location
import androidx.lifecycle.ViewModelProviders
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
import com.example.agentd.MainActivity
import com.example.agentd.R
import com.example.agentd.databinding.FragmentSigninBinding
import com.example.agentd.databinding.FragmentSignupBinding
import com.example.agentd.signup.SignupFragmentDirections
import com.example.agentd.signup.SignupViewModel
import com.example.agentd.signup.SignupViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SigninFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSigninBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signin, container, false)


        val viewModelFactory = SigninViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val signinViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SigninViewModel::class.java)

        binding.signinViewModel = signinViewModel

        binding.setLifecycleOwner(this)

        signinViewModel.sendSigninInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val email: String = binding.signinInputEmail.text.toString()
                val password: String = binding.signinInputPassword.text.toString()

                performSignin(email, password)

                var testArray : Array<Double> = (activity as MainActivity).testReturn()
                Log.d(TAG, "value1: ${testArray[0]}")
                Log.d(TAG, "value2: ${testArray[1]}")

                var location: Location? = (activity as MainActivity).getLastLocation()
//                Log.d(TAG, "latitude: ${location!!.latitude}")
//                Log.d(TAG, "longitude: ${location!!.longitude}")
//                val latitude: Double? = location!!.latitude
//                val longitude: Double? = location!!.longitude

//                var location: Array<Double?> = (activity as MainActivity).requestLastLocation()
//                val latitude: Double? = location.get(0)
//                val longitude: Double? = location.get(1)

//                binding.textLatitude.text = "Latitude: " + latitude.toString()
//                binding.textLongitude.text = "Longitude: " + longitude.toString()

                signinViewModel.doneSignin()
            }
        })

        signinViewModel.navigateToSignup.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show SignupFragment")

                this.findNavController().navigate(
                    SigninFragmentDirections
                        .actionSigninFragmentToSignupFragment()
                )

                signinViewModel.doneDoesntHaveAccount()
            }
        })

        return binding.root
    }


    fun performSignin(email: String, password: String) {
        // if empty string on authentication, app just crashed. To deal with that,
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please enter text in email/password", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "Attempt signin with email/pw: $email/$password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(!task.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d(TAG, "Successfully login user with uid: ${task.result?.user?.uid}")
            }
            .addOnFailureListener { task ->
                Toast.makeText(requireActivity(), task.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failed to login user: ${task.message}")
            }
    }

    companion object {
        val TAG = "Signin"
    }
}