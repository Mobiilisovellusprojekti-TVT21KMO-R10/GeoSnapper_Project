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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geosnapper.Events.LocationEvent
import com.example.geosnapper.Marker.MarkerToPost
import com.example.geosnapper.Post.Post
import com.example.geosnapper.Post.PostsReader
import com.example.geosnapper.Services.LocationService
import com.example.geosnapper.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.provider.Settings

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    private lateinit var client: FusedLocationProviderClient
    private var setMapOnUserLocation = true
    private var selectedMarker: Marker? = null
    private val PERMISSION_ID = 42
    private var locationPermission = false
    private var userLocation = LatLng(0.0, 0.0)

    private val posts: List<Post> by lazy {         // TÄÄ ON VÄLIAIKAINEN RATKAISU TESTAILUA VARTEN
        PostsReader(this).read()
    }


    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
        }
    }
    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        when {
            it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                        backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                }
            }
            it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        client = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        locationPermission = checkPermissions()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // TÄSSÄ ON NAPIT JOITA VOI KÄYTTÄÄ VALIKKOJEN YMS AVAAMISEEN
        binding.buttonTest1.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }
        binding.buttonTest2.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        updateMap(getLocation())
    }

    private fun updateMap(currentLocation: LatLng?) {   // KAIPAA VIILAAMISTA, EI TOIMI HYVIN NÄIN
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

    // MARKKERIEN PIIRTOETÄISYYDEN TESTAILUA VERSIO 3
    private fun calculateDistanceInMeters(post: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, post.latitude, post.longitude, results)
        return results[0]
    }

    private fun addMarkers() {      // TESTIVAIHEESSA
        val markers = MarkerToPost().listHandler(posts)
        markers.forEach { marker ->
            val distance = calculateDistanceInMeters(marker.coordinates).toString()
            val post = posts.find {it.postId == marker.postId}
            placeMarkerOnMap(marker.coordinates, post!!.message + ", Etäisyys käyttäjästä: " + distance + " m")
        }
    }

    private fun placeMarkerOnMap(coordinates: LatLng, title: String) {
        map.addMarker(MarkerOptions().position(coordinates).title(title))
    }

    // TÄÄ ON TULEVAA MARKKERIEN / POSTIEN AVAAMISTA VARTEN. VOI OLLA TURHAKIN
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
        Toast.makeText(this, "Please allow the app to use location data", Toast.LENGTH_LONG).show()
        locationPermissions.launch(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // TÄMÄ EI JOSTAIN SYYSTÄ TOIMI KUN KÄYTTÄÄ SERVICE-LUOKAN VAATIMAAN LUPIEN HAKUSYNTAKSIA. EN YMMÄRRÄ MIKSI
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startService(service)
                locationPermission = true
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): LatLng? {
        var currentLocation: LatLng? = null
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                client.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "failed to get location", Toast.LENGTH_LONG).show()
                    }
                    else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        userLocation = currentLocation as LatLng
                    }
                }
            }
            else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        return currentLocation
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        userLocation = LatLng(locationEvent.latitude!!, locationEvent.longitude!!)
        updateMap(userLocation)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }
}


