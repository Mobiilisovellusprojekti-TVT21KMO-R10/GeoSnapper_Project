package com.example.geosnapper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geosnapper.Events.LocationEvent
import com.example.geosnapper.Post.Post
import com.example.geosnapper.Post.PostsReader
import com.example.geosnapper.Services.LocationService
import com.example.geosnapper.Marker.MarkerToPost
import com.example.geosnapper.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Tasks.await
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    private lateinit var client: FusedLocationProviderClient
    private var setMapOnUserLocation = true
    private var selectedMarker: Marker? = null
    private val PERMISSION_ID = 42
    private var locationPermissions = false

    private val posts: List<Post> by lazy {         // TÄÄ ON VÄLIAIKAINEN RATKAISU TESTAILUA VARTEN
        PostsReader(this).read()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        client = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        locationPermissions = checkPermissions()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // TÄSSÄ ON NAPIT JOITA VOI KÄYTTÄÄ VALIKKOJEN YMS AVAAMISEEN
        binding.buttonTest1.setOnClickListener {
            //val intent = Intent(this, MapActivity::class.java)
            //startActivity(intent)
        }
        binding.buttonTest2.setOnClickListener {
            //val intent = Intent(this, MapActivity::class.java)
            //startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        updateMap(getLocation())
    }

    private fun updateMap(currentLocation: LatLng?) {   // EHKÄ KAIPAA VIILAAMISTA
        map.clear()
        if (currentLocation != null) {
            if (setMapOnUserLocation) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                setMapOnUserLocation = false
            }
            placeMarkerOnMap(currentLocation, "You")
            addMarkers()
        }
    }

    private fun addMarkers() {      // TESTIVAIHEESSA
        val markers = MarkerToPost().listHandler(posts)
        markers.forEach { marker ->
            //if (calculateDistance(currentLocation, post.coordinates) < drawDistance) {
            placeMarkerOnMap(marker.coordinates, "POST ID: " + marker.postId )
            //}
        }
    }

    private fun placeMarkerOnMap(coordinates: LatLng, title: String) {
        map.addMarker(MarkerOptions().position(coordinates).title(title))
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                startService(service)
                return true
        }
        requestPermissions()
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startService(service)
                locationPermissions = true
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): LatLng? {
        var currentLocation: LatLng? = null
        if (checkPermissions()) {
            client.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location == null) {
                    Toast.makeText(this, "failed to get location", Toast.LENGTH_LONG).show()
                }
                else {
                    currentLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
        return currentLocation
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        updateMap(LatLng(locationEvent.latitude!!, locationEvent.longitude!!))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }
}

