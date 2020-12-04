package com.example.agentd.missionform

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.data.User
import com.example.agentd.databinding.FragmentMissionFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.Duration
import java.util.*

class MissionFormFragment : Fragment() {

    private lateinit var missionFormViewModel: MissionFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentMissionFormBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_form, container, false
        )

        val viewModelFactory = MissionFormViewModelFactory()

        missionFormViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(MissionFormViewModel::class.java)

        binding.missionFormViewModel = missionFormViewModel

        binding.setLifecycleOwner(this)


        binding.formInputDestinationName.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        // read entered text
                        val address = binding.formInputDestinationName.text.toString()
                        Log.d(TAG, "destination name: $address")

                        val latLng: Pair<Double, Double>? = getLatLngFromAddress(requireActivity(), address)

                        if(latLng == null) {
                            Toast.makeText(requireActivity(), "$address not exists. try other address.", Toast.LENGTH_SHORT).show()
                            binding.formInputDestinationName.setText("")
                        } else {
                            Log.d(TAG, "Latitude: ${latLng!!.first}\t Longitude: ${latLng.second}")
                            binding.formInputDestinationLatitude.setText(latLng.first.toString())
                            binding.formInputDestinationLongitude.setText(latLng.second.toString())
                        }
                    }
                }
                // hide keyboard
                val imm: InputMethodManager = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return true
            }
        })



        missionFormViewModel.sendMissionInformation.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val ordererUid: String = FirebaseAuth.getInstance().uid!!
                val product: String = binding.formInputProduct.text.toString()
                val destinationName: String = binding.formInputDestinationName.text.toString()
                val destinationLatitude: Double =
                    binding.formInputDestinationLatitude.text.toString().toDouble()
                val destinationLongitude: Double =
                    binding.formInputDestinationLongitude.text.toString().toDouble()
                val receiverPhone: String = binding.formInputReceiverPhone.text.toString()
                val reward: Int = binding.formInputReward.text.toString().toInt()
                var condition1: String = binding.formInputCondition1.text.toString()
                if (condition1.isEmpty()) condition1 = "No condition specified"
                var condition2: String = binding.formInputCondition2.text.toString()
                if (condition2.isEmpty()) condition2 = "No condition specified"
                var condition3: String = binding.formInputCondition3.text.toString()
                if (condition3.isEmpty()) condition3 = "No condition specified"
                var additionalInformation: String =
                    binding.formInputAdditionalInformation.text.toString()
                if (additionalInformation.isEmpty()) additionalInformation =
                    "No information specified"

                // save mission information to databases
                saveMissionToFirebaseDatabase(
                    ordererUid, product,
                    destinationName, destinationLatitude, destinationLongitude,
                    receiverPhone, reward,
                    condition1, condition2, condition3,
                    additionalInformation
                )

                missionFormViewModel.doneCreateMission()
            }
        })

        missionFormViewModel.navigateToTitle.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    MissionFormFragmentDirections
                        .actionMissionFormFragmentToTitleFragment()
                )

                missionFormViewModel.doneNavigateToTitle()
            }
        })


        return binding.root
    }


    private fun getLatLngFromAddress(context: Context, addressString: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context, Locale.KOREAN)
        var geoResults: List<Address?>?
        var latLng: Pair<Double, Double>? = null

        try {
            geoResults = geocoder.getFromLocationName(addressString, 1)
            var i = 0
            while (geoResults!!.size == 0) {
                geoResults = geocoder.getFromLocationName(addressString, 1)
                i += 1
                if(i > 10) break
            }
            if (geoResults!!.size > 0) {
                var firstAddress: Address? = geoResults.get(0)
                latLng = Pair(firstAddress!!.latitude, firstAddress!!.longitude)
            }
        } catch (e: Exception) {
            Log.d(TAG, "$e")
        }
        return latLng
    }


    private fun saveMissionToFirebaseDatabase(
        ordererUid: String, product: String,
        destinationName: String, destinationLatitude: Double, destinationLongitude: Double,
        receiverPhone: String, reward: Int,
        condition1: String, condition2: String, condition3: String,
        additionalInformation: String
    ) {

        val ref = FirebaseDatabase.getInstance().getReference("/missions/")
        val missionId = ref.push().key.toString()

        val refOrderer = FirebaseDatabase.getInstance().getReference("/users/$ordererUid")
        refOrderer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, p0.toString())
                // read user data for getting latitude and longitude of the user
                val user: User? = p0.getValue(User::class.java)

                val condition1Complete = if (condition1 == "No condition specified") true else false
                val condition2Complete = if (condition2 == "No condition specified") true else false
                val condition3Complete = if (condition3 == "No condition specified") true else false

                // create mission instance
                val mission = Mission(
                    missionId, ordererUid, agentUid = "Agent not matched", product,
                    user!!.latitude, user!!.longitude,
                    destinationName, destinationLatitude, destinationLongitude,
                    receiverPhone,
                    reward,
                    condition1, condition1Complete,
                    condition2, condition2Complete,
                    condition3, condition3Complete,
                    additionalInformation,
                    "Wait for agent",
                    false, false
                )

                // go back to title fragment
                missionFormViewModel.onNavigateToTitle()

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