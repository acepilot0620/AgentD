package com.example.agentd.user

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
import com.example.agentd.data.User
import com.example.agentd.databinding.FragmentUserBinding
import com.example.agentd.signin.SigninFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserFragment : Fragment() {

    private lateinit var uid: String

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

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        binding.setLifecycleOwner(this)

        // fetch user information
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, p0.toString())
                val user: User? = p0.getValue(User::class.java)

                // show saved value to user
                binding.edittextUsername.setText(user!!.username)
                binding.edittextEmail.setText(user!!.email)
                binding.edittextPhoneNumber.setText(user!!.phoneNumber)
                binding.edittextBalance.setText(user!!.balance.toString())
                binding.textLatitudeUserMain.setText(user!!.latitude.toString())
                binding.textLongitudeUserMain.setText(user!!.longitude.toString())

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        userViewModel.navigateToTitle.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                // update edited user information
                updateEditUserInformation(
                    uid,
                    binding.edittextUsername.text.toString(),
                    binding.edittextPhoneNumber.text.toString(),
                    binding.edittextBalance.text.toString().toLong())

                // navigate back to title fragment
                this.findNavController().navigate(
                    UserFragmentDirections
                        .actionUserFragmentToTitleFragment()
                )
                userViewModel.doneBackToTitle()
            }
        })

        userViewModel.signout.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                // Sign out using google auth
                performSignout()

                // navigate back to signin fragment
                this.findNavController().navigate(
                    UserFragmentDirections
                        .actionUserFragmentToSigninFragment()
                )
                userViewModel.doneSignout()
            }
        })



        return binding.root
    }

    private fun performSignout() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun updateEditUserInformation(uid: String, username: String, phoneNumber: String, balance: Long) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val updates = hashMapOf<String, Any>()

        Log.d(TAG, "[updateEditUserInformation] uid: ${uid}")
        Log.d(TAG, "[updateEditUserInformation] new username: ${username}")
        Log.d(TAG, "[updateEditUserInformation] new phone number: ${phoneNumber}")
        Log.d(TAG, "[updateEditUserInformation] new balance: ${balance}")

        updates.put("username", username)
        updates.put("phoneNumber", phoneNumber)
        updates.put("balance", balance)

        ref.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update user information for $uid")
            }
            .addOnFailureListener {
                // for logging purposes
            }

    }

    companion object {
        private const val TAG = "User"
    }

}