package com.example.agentd.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.MainActivity
import com.example.agentd.R
import com.example.agentd.databinding.FragmentSignupBinding

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

                Log.d("SignupFragment", "Username is: " + username)
                Log.d("SignupFragment", "email is: " + email)
                Log.d("SignupFragment", "password is: " + password)

                // Firebase Authentication to create a user with email and password


                signupViewModel.doneSignup()


            }
        })

        signupViewModel.navigateToSignin.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d("SignupFragment", "Try to show LoginFragment")

                this.findNavController().navigate(
                    SignupFragmentDirections
                        .actionSignupFragmentToSigninFragment()
                )

                signupViewModel.doneAlreadyHaveAccount()
            }
        })

        return binding.root
    }

//    fun authenticateFirebase(email: String, password: String): String? {
//        return (activity as MainActivity).authenticateFirebase(email, password)
//    }


}