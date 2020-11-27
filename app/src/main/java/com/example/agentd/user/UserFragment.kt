package com.example.agentd.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentUserBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user, container, false)

        val viewModelFactory = UserViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val userViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(UserViewModel::class.java)

        binding.userViewModel = userViewModel

        binding.setLifecycleOwner(this)

//        userViewModel.navigateToSignup.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                this.findNavController()
//            }
//        })



        return binding.root
    }

}