package com.example.agentd.signin

import androidx.lifecycle.ViewModelProviders
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
import com.example.agentd.R
import com.example.agentd.databinding.FragmentSigninBinding
import com.example.agentd.databinding.FragmentSignupBinding
import com.example.agentd.signup.SignupFragmentDirections
import com.example.agentd.signup.SignupViewModel
import com.example.agentd.signup.SignupViewModelFactory

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

        signinViewModel.sendSignInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val email: String = binding.signinInputEmail.text.toString()
                val password: String = binding.signinInputPassword.text.toString()

                Log.d("SigninFragment", "Attempt signin with email/pw: $email/$password")

                signinViewModel.doneSignin()
            }
        })

        signinViewModel.navigateToSignup.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d("SignupFragment", "Try to show LoginFragment")

                this.findNavController().navigate(
                    SigninFragmentDirections
                        .actionSigninFragmentToSignupFragment()
                )

                signinViewModel.doneDoesntHaveAccount()
            }
        })

        return binding.root
    }




}