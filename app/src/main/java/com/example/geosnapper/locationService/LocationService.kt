package com.example.geosnapper.locationService

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus

class LocationService : Service() {
    private var client: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setIntervalMillis(5000)
            .build()
        locationCallback = object: LocationCallback(){
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }
    }

    @Suppress("MissingPermission")
    fun locationRequest(){
        try {
            client?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        EventBus.getDefault().post(
            LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        )
        )
    }

    private fun stopLocationUpdates(){
        locationCallback?.let {
            client?.removeLocationUpdates(it)
        }
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        locationRequest()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent): IBinder? = null
}