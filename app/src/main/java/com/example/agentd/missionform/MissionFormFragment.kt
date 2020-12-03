package com.example.agentd.missionform

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
import com.example.agentd.data.Mission
import com.example.agentd.data.MissionCondition
import com.example.agentd.data.MissionExtend
import com.example.agentd.data.User
import com.example.agentd.databinding.FragmentMissionFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MissionFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentMissionFormBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_form, container, false)

        val viewModelFactory = MissionFormViewModelFactory()

        val missionFormViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MissionFormViewModel::class.java)

        binding.missionFormViewModel = missionFormViewModel

        binding.setLifecycleOwner(this)

        missionFormViewModel.sendMissionInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val ordererUid: String = FirebaseAuth.getInstance().uid!!
                val product: String = binding.formInputProduct.text.toString()
                val destinationName: String = binding.formInputDestinationName.text.toString()
                val destinationLatitude: Double = binding.formInputDestinationLatitude.text.toString().toDouble()
                val destinationLongitude: Double = binding.formInputDestinationLongitude.text.toString().toDouble()
                val receiverPhone: String = binding.formInputReceiverPhone.text.toString()
                val reward: Int = binding.formInputReward.text.toString().toInt()
                var condition1: String = binding.formInputCondition1.text.toString()
                if(condition1.isEmpty()) condition1 = "No condition specified"
                var condition2: String = binding.formInputCondition2.text.toString()
                if(condition2.isEmpty()) condition2 = "No condition specified"
                var condition3: String = binding.formInputCondition3.text.toString()
                if(condition3.isEmpty()) condition3 = "No condition specified"
                val additionalInformation: String = binding.formInputAdditionalInformation.text.toString()

                // save mission information to databases
                saveMissionToFirebaseDatabase(
                    ordererUid, product,
                    destinationName, destinationLatitude, destinationLongitude,
                    receiverPhone, reward,
                    condition1, condition2, condition3,
                    additionalInformation
                )

                missionFormViewModel.doneCreateMission()

                // go back to title fragment
                missionFormViewModel.onNavigateToTitle()
            }
        })

        missionFormViewModel.navigateToTitle.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                this.findNavController().navigate(
                    MissionFormFragmentDirections
                        .actionMissionFormFragmentToTitleFragment()
                )

                missionFormViewModel.doneNavigateToTitle()
            }
        })


        return binding.root
    }

    private fun saveMissionToFirebaseDatabase(ordererUid: String, product: String,
                                              destinationName: String, destinationLatitude: Double, destinationLongitude: Double,
                                              receiverPhone: String, reward: Int,
                                              condition1: String, condition2: String, condition3: String,
                                              additionalInformation: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/missions/")
        val missionId = ref.push().key.toString()

        val refOrderer = FirebaseDatabase.getInstance().getReference("/users/$ordererUid")
        refOrderer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, p0.toString())
                // read user data for getting latitude and longitude of the user
                val user: User? = p0.getValue(User::class.java)

                // create mission instance
                val mission = MissionExtend(
                    missionId, ordererUid, agentUid = "", product,
                    user!!.latitude, user!!.longitude,
                    destinationName, destinationLatitude, destinationLongitude,
                    receiverPhone,
                    reward,
                    MissionCondition(condition1, false),
                    MissionCondition(condition2, false),
                    MissionCondition(condition3, false),
                    additionalInformation
                )

                // hand over mission instance to database
                ref.child(missionId).setValue(mission)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully saved mission data")
                    }
                    .addOnFailureListener {
                        // for logging purposes
                    }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    companion object {
        private const val TAG = "MissionForm"
    }

}