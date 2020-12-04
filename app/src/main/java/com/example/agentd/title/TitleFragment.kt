package com.example.agentd.title

import android.icu.text.CaseMap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.data.MissionExtend
import com.example.agentd.databinding.FragmentTitleBinding
import com.example.agentd.missiondetail.MissionDetailFragment
import com.example.agentd.signin.SigninFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class TitleFragment : Fragment(), OnMapReadyCallback {

    private lateinit var titleViewModel: TitleViewModel
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // setHasOptionsMenu(true)


        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentTitleBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_title, container, false)

        val viewModelFactory = TitleViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        titleViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(TitleViewModel::class.java)

        binding.titleViewModel = titleViewModel

        binding.setLifecycleOwner(this)

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        // get ready for naver map location tracking
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance(NaverMapOptions().locationButtonEnabled(true)).also {
                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)


        // Down navigation button bar

        titleViewModel.navigateToUser.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show UserFragment")

                this.findNavController().navigate(
                    TitleFragmentDirections
                        .actionTitleFragmentToUserFragment()
                )

                titleViewModel.doneNavigateToUser()
            }
        })

        titleViewModel.navigateToMissionForm.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show MissionFormFragment")

                this.findNavController().navigate(
                    TitleFragmentDirections
                        .actionTitleFragmentToMissionFormFragment()
                )

                titleViewModel.doneNavigateToMissionForm()
            }
        })


        titleViewModel.navigateToMissionDetail.observe(viewLifecycleOwner, Observer { mission ->
            // definately need null check "?"
            mission?.let {

                Log.d(TAG, "Try to show MissionDetailFragment")
                val directions: NavDirections

                if (mission.ordererUid == uid) {
                    directions = TitleFragmentDirections.actionTitleFragmentToMissionDetailFragment(
                        missionId = mission.missionId,
                        fromTitle = false,
                        fromOrderer = true,
                        fromAgent = false
                    )
                } else if (mission.agentUid == uid) {
                    directions = TitleFragmentDirections.actionTitleFragmentToMissionDetailFragment(
                        missionId = mission.missionId,
                        fromTitle = false,
                        fromOrderer = false,
                        fromAgent = true
                    )
                } else {
                    directions = TitleFragmentDirections.actionTitleFragmentToMissionDetailFragment(
                        missionId = mission.missionId,
                        fromTitle = true,
                        fromOrderer = false,
                        fromAgent = false
                    )
                }

                this.findNavController().navigate(directions)

                titleViewModel.doneNavigateToMissionDetail()
            }
        })


        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                map.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // setMarkers(naverMap)
        // Always MARKER FIRST!
        val ref = FirebaseDatabase.getInstance().getReference("/missions/")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach cont@{
                    Log.d(TAG, "Successfully load data from database: ${it}")
                    val mission : Mission? = it.getValue(Mission::class.java)

                    if(mission!!.confirmed) return@cont // do not display confirmed missions

                    // For missions already starting delivery,
                    // if the user is not related to the mission, do not show those missions
                    if(mission.status != "Wait for agent") {
                        if (mission.ordererUid != uid && mission.agentUid != uid) return@cont
                    }

                    // set marker
                    val marker = Marker()
                    marker.position = LatLng(mission.sourceLatitude, mission.sourceLongitude)

                    if (mission.ordererUid == uid) marker.icon = MarkerIcons.BLUE
                    else if (mission.agentUid == uid) marker.icon = MarkerIcons.RED

                    marker.setOnClickListener {
                        titleViewModel.onNavigateToMissionDetail(mission)

                        marker.icon = MarkerIcons.LIGHTBLUE
                        false
                    }
                    marker.map = naverMap

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Failed to load data from database: ", databaseError.toException())
            }
        })

        map = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        Log.d(TAG, "Ready for load the map")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "Title"
    }

}