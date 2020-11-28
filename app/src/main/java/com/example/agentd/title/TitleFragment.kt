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
import com.example.agentd.databinding.FragmentTitleBinding
import com.example.agentd.signin.SigninFragmentDirections
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource

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
                Log.d(TAG, "Try to show SignupFragment")

                this.findNavController().navigate(
                    TitleFragmentDirections
                        .actionTitleFragmentToUserFragment()
                )

                titleViewModel.doneNavigateToUser()
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
        map = naverMap

        naverMap.locationSource = locationSource

        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "Title"
    }

}