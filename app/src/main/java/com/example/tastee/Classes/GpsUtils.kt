package com.example.tastee.Classes

import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


class GpsUtils() {
    private var context: Context? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var locationManager: LocationManager? = null
    private var locationRequest: LocationRequest? = null


    constructor(context: Context) : this() {
        this.context = context
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSettingsClient = LocationServices.getSettingsClient(context)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 10 * 1000
        locationRequest!!.fastestInterval = 2 * 1000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()

        builder.setAlwaysShow(true) //this is the key ingredient
    }

    fun turnGPSOn(onGpsListener: onGpsListener?) {

        mSettingsClient!!
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener((context as Activity?)!!) { //  GPS is already enable, callback GPS status through listener
                onGpsListener?.gpsStatus(true)
            }
            .addOnFailureListener(
                (context as Activity?)!!
            ) { e ->
                val statusCode: Int = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(), and check the
                        // result in onActivityResult().
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(
                            context as Activity?,
                            3
                        )
                    } catch (sie: SendIntentException) {
                        Log.i("tag", "PendingIntent unable to execute request.")
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        Log.e("tag", errorMessage)
                        Toast.makeText(context as Activity?, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

    }


    interface onGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }
}