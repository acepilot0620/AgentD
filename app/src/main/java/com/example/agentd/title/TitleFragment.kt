package com.example.agentd.title

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.data.MissionExtend
import com.example.agentd.databinding.FragmentTitleBinding
import com.example.agentd.signin.SigninFragmentDirections
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

    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // setHasOptionsMenu(true)


        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentTitleBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_title, container, false)

        val viewModelFactory = TitleViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val titleViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(TitleViewModel::class.java)

        binding.titleViewModel = titleViewModel

        binding.setLifecycleOwner(this)

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
                Log.d(TAG, "Try to move MissionFormFragment")

                this.findNavController().navigate(
                    TitleFragmentDirections
                        .actionTitleFragmentToMissionFormFragment()
                )

                titleViewModel.doneNavigateToMissionForm()
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
        // Always MARKER FIRST!
        val ref = FirebaseDatabase.getInstance().getReference("/missions/")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d(TAG, "Successfully load data from database: ${it}")
                    val mission : Mission? = it.getValue(Mission::class.java)

                    Marker().apply {
                        position = LatLng(mission!!.sourceLatitude, mission!!.sourceLongitude)
                        map = naverMap
                    }
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

    private fun setMarkers(naverMap: NaverMap){
        val ref = FirebaseDatabase.getInstance().getReference("/missions/")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d(TAG, "Successfully load data from database: ${it}")
                    val mission : Mission? = it.getValue(Mission::class.java)

                    val marker =  Marker()
                    marker.position = LatLng(mission!!.sourceLatitude,mission!!.sourceLatitude)
                    marker.map = naverMap
                    marker.icon = MarkerIcons.BLACK
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Failed to load data from database: ", databaseError.toException())
            }
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "Title"
    }

}