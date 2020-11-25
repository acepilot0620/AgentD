package com.example.agentd.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        signupViewModel.sendInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val email: String = binding.signupIdInput.text.toString()
                val password: String = binding.signupPwdInput.text.toString()
                val endMessage: String?

                endMessage = signupViewModel.authenticateFirebase(email, password)
                signupViewModel.doneSignup()

                binding.signupMessage.text = endMessage
            }
        })

        return binding.root
    }


}