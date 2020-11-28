package com.example.agentd

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val toolbar = findViewById<Toolbar>(R.id.top_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermissions()) {
            // Permission is not yet granted
            Log.d(TAG, "[onCreate] Permission is not yet granted")
            requestLocationPermissions()
        }


    }

    fun testReturn() : Array<Double> {
        return arrayOf(1.1, 2.2)
    }

//    fun requestLastLocation(): Array<Double?> {
//        var location : Location? = null
//        var latitude: Double? = null
//        var longitude: Double? = null
//
//        if (checkPermissions()) {
//            // Permission is not yet granted
//            Log.d(TAG, "Permission is not yet granted")
//            requestLocationPermissions()
//        } else {
//            // Permission is granted
//            Log.d(TAG, "Permission is granted")
//            location = getLastLocation()
//            latitude = location!!.latitude
//            longitude = location!!.longitude
//        }
//        return arrayOf(latitude, longitude)
//    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() : Location?{
        var location: Location? = null

        if (checkPermissions()) {
            // Permission is not yet granted
            Log.d(TAG, "Permission is not yet granted")
            requestLocationPermissions()
        } else {
            // Permission is granted
            Log.d(TAG, "Permission is granted")
            this.fusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.d(TAG, "Successfully get location")
                        location = task.result
                        Log.d(TAG, "latitude: ${location!!.latitude}")
                        Log.d(TAG, "longitude: ${location!!.longitude}")
                    } else {
                        Log.d(TAG, "getLastLocation:exception", task.exception)
                        Toast.makeText(this, "Failed to detect location", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        return location
    }

//    @SuppressLint("MissingPermission")
//    private fun getLastLocation() : Location?{
//        var location: Location? = null
//
//        fusedLocationClient.lastLocation
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result != null) {
//                    location = task.result
//                    Log.d(TAG, "latitude: ${location!!.latitude}")
//                    Log.d(TAG, "longitude: ${location!!.longitude}")
//                } else {
//                    Log.d(TAG, "getLastLocation:exception", task.exception)
//                    Toast.makeText(this, "Failed to detect location", Toast.LENGTH_SHORT).show()
//                }
//            }
//        return location
//    }

    private fun checkPermissions(): Boolean {
        val permissionStateFine: Boolean = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "FINE permission not granted: ${permissionStateFine.toString()}")
        val permissionStateCoarse: Boolean = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "COARSE permission not granted: ${permissionStateCoarse.toString()}")
        return permissionStateFine && permissionStateCoarse
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
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
        const val TAG = "Location"
    }


}