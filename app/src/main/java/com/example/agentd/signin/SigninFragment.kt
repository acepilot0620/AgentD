package com.example.agentd.signin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agentd.MainActivity
import com.example.agentd.R
import com.example.agentd.databinding.FragmentSigninBinding
import com.example.agentd.databinding.FragmentSignupBinding
import com.example.agentd.signup.SignupFragment
import com.example.agentd.signup.SignupFragmentDirections
import com.example.agentd.signup.SignupViewModel
import com.example.agentd.signup.SignupViewModelFactory
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import java.util.*

class SigninFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var uid: String

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

        if (checkPermissions()) {
            // Permission is not yet granted
            Log.d(TAG, "[onCreate] Permission is not yet granted")
            requestLocationPermissions()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        signinViewModel.sendSigninInformation.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                val email: String = binding.signinInputEmail.text.toString()
                val password: String = binding.signinInputPassword.text.toString()

                // perform signin and if success, save location data to database
                performSignin(email, password)

                signinViewModel.doneSignin()
            }
        })

        signinViewModel.navigateToSignup.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show SignupFragment")

                this.findNavController().navigate(
                    SigninFragmentDirections
                        .actionSigninFragmentToSignupFragment()
                )

                signinViewModel.doneDoesntHaveAccount()
            }
        })

        return binding.root
    }


    fun performSignin(email: String, password: String) {
        // if empty string on authentication, app just crashed. To deal with that,
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please enter text in email/password", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "Attempt signin with email/pw: $email/$password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(!task.isSuccessful) return@addOnCompleteListener

                // else if successful
                uid = task.result?.user?.uid.toString()
                Log.d(TAG, "Successfully login user with uid: ${task.result?.user?.uid}")

                // update location
                getLastLocationAndUpdateCurrentLocation()
            }
            .addOnFailureListener { task ->
                Toast.makeText(requireActivity(), task.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failed to login user: ${task.message}")
            }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocationAndUpdateCurrentLocation() {
        var lastLocation: Location?

        if (checkPermissions()) {
            // Permission is not yet granted
            Log.d(TAG, "Permission is not yet granted")
            requestLocationPermissions()
        } else {
            // Permission is granted
            Log.d(TAG, "Permission is granted")
            fusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.d(TAG, "Successfully get last location")
                        lastLocation = task.result
                        Log.d(TAG, "[saveLastLocation] last latitude: ${lastLocation!!.latitude}")
                        Log.d(TAG, "[saveLastLocation] last longitude: ${lastLocation!!.longitude}")

                        // update to current location
                        getCurrentLocation()

                    } else {
                        Log.d(TAG, "getLastLocation:exception", task.exception)
                        Toast.makeText(requireActivity(), "Failed to detect location", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        return
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var currentLocation: Location = locationResult.lastLocation
            var currentLatitude: Double = currentLocation!!.latitude
            var currentLongitude: Double = currentLocation!!.longitude


            Log.d(TAG, "[getCurrentLocation] current latitude: ${currentLatitude}")
            Log.d(TAG, "[getCurrentLocation] current longitude: ${currentLongitude}")

            // print location name
            // getCityName(currentLatitude, currentLongitude) // Sometimes not working. Currently disable

            // update current location to database
            updateCurrentLocation(uid, currentLatitude, currentLongitude)

        }
    }


    private fun getCityName(latitude: Double,longitude: Double) {
        var countryName: String
        var cityName: String
        var geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        var address = geoCoder.getFromLocation(latitude, longitude,3)

        countryName = address.get(0).countryName
        cityName = address.get(0).locality

        Log.d(TAG,"Country: " + countryName + " City: " + cityName )
    }


    private fun updateCurrentLocation(uid: String, latitude: Double?, longitude: Double?) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val updates = hashMapOf<String, Any>()

        Log.d(TAG, "[updateLastLocation] uid: ${uid}")
        Log.d(TAG, "[updateLastLocation] current latitude: ${latitude}")
        Log.d(TAG, "[updateLastLocation] current longitude: ${longitude}")

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


    fun checkPermissions(): Boolean {
        val permissionStateFine: Boolean = ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "FINE permission not granted: ${permissionStateFine.toString()}")
        val permissionStateCoarse: Boolean = ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "COARSE permission not granted: ${permissionStateCoarse.toString()}")
        return permissionStateFine && permissionStateCoarse
    }


    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_CODE)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow in your app.
                    Log.d(TAG, "Permission granted")
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Log.d(TAG, "Permission denied once")
                        requestLocationPermissions()
                    } else {
                        Log.d(TAG, "Permission denied forever")
                    }
                }
            }
        }
    }


    companion object {
        const val PERMISSION_REQUEST_CODE = 1617
        const val TAG = "Signin"
    }

}