package com.example.agentd.missiondetail

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.data.User
import com.example.agentd.databinding.FragmentMissionDetailBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class MissionDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var missionDetailViewModel: MissionDetailViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap
    private lateinit var uid: String
    private lateinit var missionId: String

    private var fromAgent: Boolean? = null
    private var fromOrderer: Boolean? = null
    private var reward: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_detail, container, false)

        val arguments = MissionDetailFragmentArgs.fromBundle(requireArguments())
        fromAgent = arguments.fromAgent
        fromOrderer = arguments.fromOrderer
        // print mission on layout
        missionId = arguments.missionId.toString() // null case
        Log.d(TAG, "Successfully get missionId: $missionId")

        // val fragmentManager = requireActivity().supportFragmentManager

        val viewModelFactory = MissionDetailViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        missionDetailViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MissionDetailViewModel::class.java)

        binding.missionDetailViewModel = missionDetailViewModel

        binding.setLifecycleOwner(this)

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment: MapFragment

        if(arguments.fromTitle) {
            // Accept button only
            binding.detailMapFragment.visibility = View.GONE
            binding.detailButtonComplete.visibility = View.GONE
            binding.detailButtonConfirm.visibility = View.GONE
            binding.detailCheckboxCondition1.isEnabled = false
            binding.detailCheckboxCondition2.isEnabled = false
            binding.detailCheckboxCondition3.isEnabled = false
        } else if(arguments.fromAgent) {
            // Complete button only
            binding.detailMapFragment.visibility = View.GONE
            binding.detailButtonAccept.visibility = View.GONE
            binding.detailButtonConfirm.visibility = View.GONE

            // start update location
            requestLocationUpdates()

        } else if(arguments.fromOrderer) {
            // Map for tracking and
            binding.detailButtonAccept.visibility = View.GONE
            binding.detailButtonComplete.visibility = View.GONE
            binding.detailCheckboxCondition1.isEnabled = false
            binding.detailCheckboxCondition2.isEnabled = false
            binding.detailCheckboxCondition3.isEnabled = false
            binding.detailButtonConfirm.isEnabled = false

            // get ready for naver map location tracking
            // The map also contains agent's current location (expressed by marker)
            mapFragment = childFragmentManager.findFragmentById(R.id.detail_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(NaverMapOptions().locationButtonEnabled(false)).also {
                    childFragmentManager.beginTransaction().add(R.id.detail_map_fragment, it).commit()
                }
            mapFragment.getMapAsync(this)

            locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        }


        // fetch mission information
        val ref = FirebaseDatabase.getInstance().getReference("/missions/$missionId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, p0.toString())
                val mission: Mission? = p0.getValue(Mission::class.java)

                // show saved value to user
                binding.detailTextProduct.setText(mission!!.product)
                reward = mission!!.reward.toString().toInt()
                Log.d(TAG, "The reward for mission $missionId is $reward")
                binding.detailTextReward.setText(mission!!.reward.toString())
                binding.detailTextDestination.setText(mission!!.destinationName)

                binding.detailTextCondition1.setText(mission!!.condition1)
                binding.detailCheckboxCondition1.isChecked = mission!!.condition1Complete
                if(mission!!.condition1 == "No condition specified") binding.detailCheckboxCondition1.isEnabled = false

                binding.detailTextCondition2.setText(mission!!.condition2)
                binding.detailCheckboxCondition2.isChecked = mission!!.condition2Complete
                if(mission!!.condition2 == "No condition specified") binding.detailCheckboxCondition2.isEnabled = false

                binding.detailTextCondition3.setText(mission!!.condition3)
                binding.detailCheckboxCondition3.isChecked = mission!!.condition3Complete
                if(mission!!.condition3 == "No condition specified") binding.detailCheckboxCondition3.isEnabled = false

                binding.detailTextAdditionalInformation.setText(mission!!.additionalInformation)

                binding.detailTextStatus.setText(mission!!.status)

                // when the agent wait for confirmation from orderer
                if(fromAgent!!) {
                    if(mission!!.completed && !mission!!.confirmed) {
                        binding.detailTextStatus.setText(getString(R.string.status_ask_for_confirm))
                        binding.detailButtonComplete.isEnabled = false
                        binding.detailButtonComplete.visibility = View.GONE

                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.child("confirmed").getValue() == true) {
                                    binding.detailTextStatus.setText(p0.child("status").getValue().toString())

                                    // get money
                                    updateUserBalance(uid, reward, true)

                                    missionDetailViewModel.onNavigateToTitle()
                                }
                            }
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d(TAG, "Failed to get mission information for updating completion")
                            }

                        })
                    } else if (mission!!.completed && mission!!.confirmed) {
                        // if agent see the order history (already completed and confirmed
                        binding.detailButtonCall.isClickable = false
                        binding.detailCheckboxCondition1.isEnabled = false
                        binding.detailCheckboxCondition2.isEnabled = false
                        binding.detailCheckboxCondition3.isEnabled = false
                        binding.detailButtonComplete.isEnabled = false
                        binding.detailButtonComplete.visibility = View.GONE
                    }
                } else if (fromOrderer!!) {
                    if (mission!!.completed && mission!!.confirmed) {
                        binding.detailButtonConfirm.isEnabled = false
                        binding.detailButtonConfirm.visibility = View.GONE
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Failed to get mission information")

            }
        })

        val checkboxes = listOf(
            Pair("condition1Complete", binding.detailCheckboxCondition1),
            Pair("condition2Complete", binding.detailCheckboxCondition2),
            Pair("condition3Complete", binding.detailCheckboxCondition3)
        )

        if(arguments.fromOrderer) {
            // realtime update for checkbox
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    // if somebody accepts the mission
                    binding.detailTextStatus.setText(p0.child("status").getValue().toString())

                    // if the agent complete the mission, enable confirm button
                    // and update changed status
                    if(p0.child("completed").getValue() == true) {
                        binding.detailTextStatus.setText(p0.child("status").getValue().toString())
                        binding.detailButtonConfirm.isEnabled = true

                    }

                    // checking check box status
                    checkboxes.forEach {
                        if(p0.child(it.first).getValue() == true) {
                            it.second.isChecked = true
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, "Failed to get mission information for updating checkbox")
                }

            })
        } else if(arguments.fromAgent) {
            missionDetailViewModel.checkBoxChecked.observe(viewLifecycleOwner, Observer {
                it?.let {
                    Log.d(TAG, "$it")
                    val modList = mutableListOf<Pair<String, Any>>()
                    modList.add(Pair(checkboxes[it].first, true))
                    updateMissionInformationByField(missionId, modList, false)

                    missionDetailViewModel.doneCheckBoxChecked()
                }
            })
        } else if(arguments.fromTitle) {
            // for ordinary user, if agent occurs when they watch mission detail,
            ref.child("agentUid").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    // get back to title
                    if(p0.getValue() != "Agent not matched") missionDetailViewModel.onNavigateToTitle()
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, "Failed to get mission information for updating checkbox")
                }

            })
        }


        // for agent
        missionDetailViewModel.acceptMission.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val modList = mutableListOf<Pair<String, Any>>()
                modList.add(Pair("agentUid", uid))
                modList.add(Pair("status", getString(R.string.status_on_delivery)))

                // remove accept button
                binding.detailButtonAccept.visibility = View.GONE
                // now he can press accept button
                binding.detailButtonComplete.visibility = View.VISIBLE
                // enable checkbox
                if(binding.detailTextCondition1.text != "No condition specified") binding.detailCheckboxCondition1.isEnabled = true
                if(binding.detailTextCondition2.text != "No condition specified") binding.detailCheckboxCondition2.isEnabled = true
                if(binding.detailTextCondition3.text != "No condition specified") binding.detailCheckboxCondition3.isEnabled = true

                // set status to On delivery
                binding.detailTextStatus.setText(getString(R.string.status_on_delivery))

                // update that the user accept the order
                updateMissionInformationByField(missionId.toString(), modList, true)

                missionDetailViewModel.doneAcceptMission()
            }
        })

        // for agent
        missionDetailViewModel.completeMission.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val modList = mutableListOf<Pair<String, Any>>()
                modList.add(Pair("completed", true))
                modList.add(Pair("status", getString(R.string.status_complete)))

                binding.detailButtonComplete.visibility = View.GONE

                updateMissionInformationByField(missionId.toString(), modList, false)

                binding.detailTextStatus.setText(getString(R.string.status_ask_for_confirm))


                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.child("confirmed").getValue() == true) {
                            binding.detailTextStatus.setText(p0.child("status").getValue().toString())

                            // get money
                            updateUserBalance(uid, reward, true)

                            missionDetailViewModel.onNavigateToTitle()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d(TAG, "Failed to get mission information for updating completion")
                    }

                })

                missionDetailViewModel.doneCompleteMission()
            }
        })

        // for orderer
        missionDetailViewModel.confirmMissionResult.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val modList = mutableListOf<Pair<String, Any>>()
                modList.add(Pair("confirmed", true))
                modList.add(Pair("status", getString(R.string.status_confirmed)))

                updateMissionInformationByField(missionId.toString(), modList, true)

                // pay for money
                updateUserBalance(uid, reward, false)

                missionDetailViewModel.doneConfirmMissionResult()
            }
        })


        missionDetailViewModel.navigateToTitle.observe(viewLifecycleOwner, Observer {
            if(it == true) {

                this.findNavController().navigate(
                    MissionDetailFragmentDirections
                        .actionMissionDetailFragmentToTitleFragment()
                )
                // fragmentManager.popBackStack()
                missionDetailViewModel.doneNavigateToTitle()
            }
        })



        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if(fromAgent!!) stopLocationUpdates()
    }


    // For orderer only
    override fun onMapReady(naverMap: NaverMap) {

        // draw route line from source to destination
        FirebaseDatabase.getInstance().getReference("/missions/$missionId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    // make coordinate list for route
                    val coords = mutableListOf<LatLng>(
                        LatLng(
                            p0.child("sourceLatitude").getValue().toString().toDouble(),
                            p0.child("sourceLongitude").getValue().toString().toDouble()),
                        LatLng(
                            p0.child("destinationLatitude").getValue().toString().toDouble(),
                            p0.child("destinationLongitude").getValue().toString().toDouble())
                    )
                    // draw path
                    PathOverlay().also {
                        it.coords = coords
                        it.width = 6
                        it.outlineWidth = 0
                        it.color = Color.RED
                        it.patternImage = OverlayImage.fromResource(R.drawable.path_pattern)
                        it.patternInterval = resources.getDimensionPixelSize(R.dimen.overlay_pattern_interval)
                        it.map = naverMap
                    }
                    // move focus to source
                    val cameraUpdate = CameraUpdate.scrollTo(
                        LatLng(
                            p0.child("sourceLatitude").getValue().toString().toDouble(),
                            p0.child("sourceLongitude").getValue().toString().toDouble()))
                            .animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, "Change listener for route failed")
                }
            })

        // get uid for agent
        val ref = FirebaseDatabase.getInstance().getReference("/missions/$missionId")
        ref.child("agentUid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val agentUid = p0.getValue().toString()
                Log.d(TAG, "Get agent $agentUid 's location")

                if(agentUid != "Agent not matched") {
                    // reference agent's current location and show it on the map
                    val refUser = FirebaseDatabase.getInstance().getReference("/users/$agentUid")
                    refUser.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            Log.d(TAG, p0.toString())
                            val user: User? = p0.getValue(User::class.java)

                            val latitude: Double = user!!.latitude
                            val longitude: Double = user!!.longitude
                            Log.d(TAG, "Agent ${user.uid} location update (latitude: $latitude longitude: $longitude)")

                            // set marker
                            val marker = Marker()
                            marker.position = LatLng(latitude, longitude)
                            // marker.icon = OverlayImage.fromResource(R.drawable.circle_shadow)
                            marker.icon = MarkerIcons.RED
                            marker.width = Marker.SIZE_AUTO
                            marker.height = Marker.SIZE_AUTO
                            marker.map = naverMap

                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                                .animate(CameraAnimation.Easing)
                            naverMap.moveCamera(cameraUpdate)

                        }
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d(TAG, "Change listener for marker failed")
                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Failed to load agentUid from database: ", p0.toException())
            }
        })

         map = naverMap
         // naverMap.locationSource = locationSource
         // naverMap.locationTrackingMode = LocationTrackingMode.Follow

        Log.d(TAG, "Ready for load the map")
    }


    private fun <T: Any> updateMissionInformationByField(missionId: String, contents: List<Pair<String, T>>, backToTitle: Boolean) {
        val ref = FirebaseDatabase.getInstance().getReference("/missions/$missionId")
        val updates = hashMapOf<String, Any>()

        Log.d(TAG, "[updateMissionInformationOneField] missionId: ${missionId}\t uid: ${uid}")

        contents.forEach {
            updates.put(it.first, it.second)
        }

        if(backToTitle) missionDetailViewModel.onNavigateToTitle()

        ref.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update database")

            }
            .addOnFailureListener {
                // for logging purposes
            }

    }

    private fun updateUserBalance(userId: String, amount: Int, agentReceiver: Boolean) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.child("balance").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val balance: Int = p0.getValue().toString().toInt()
                Log.d(TAG, "User $userId 's balance is $balance")

                val updates = hashMapOf<String, Any>()
                if(agentReceiver) {
                    // agent receives money
                    updates.put("balance", balance + amount)
                } else {
                    // orderer through out money
                    updates.put("balance", balance - amount)
                }

                ref.updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully update balance for $userId")

                    }
                    .addOnFailureListener {
                        // for logging purposes
                    }

            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Failed to read user $userId 's balance")
            }

        })


    }


    private fun requestLocationUpdates() {
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 7500
        locationRequest.fastestInterval = 5000

        val permission: Int = ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)

        if(permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
            )
        }
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "done updating location of agent")
    }



    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var currentLocation: Location = locationResult.lastLocation
            var currentLatitude: Double = currentLocation.latitude
            var currentLongitude: Double = currentLocation.longitude


            Log.d(TAG, "[requestLocationUpdates] current latitude: ${currentLatitude}")
            Log.d(TAG, "[requestLocationUpdates] current longitude: ${currentLongitude}")

            // update current location to database
            updateCurrentLocation(uid, currentLatitude, currentLongitude)

        }
    }


    private fun updateCurrentLocation(uid: String, latitude: Double?, longitude: Double?) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val updates = hashMapOf<String, Any>()

        Log.d(TAG, "[updateCurrentLocation] uid: ${uid}")
        Log.d(TAG, "[updateCurrentLocation] current latitude: ${latitude}")
        Log.d(TAG, "[updateCurrentLocation] current longitude: ${longitude}")

        updates.put("latitude", latitude!!)
        updates.put("longitude", longitude!!)

        ref.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update location for $uid")
            }
            .addOnFailureListener {
                // for logging purposes
            }

    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 200
        private const val TAG = "MissionDetail"
    }


}