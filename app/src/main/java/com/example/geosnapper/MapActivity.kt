package com.example.geosnapper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geosnapper.Events.LocationEvent
import com.example.geosnapper.Post.Post
import com.example.geosnapper.Post.PostsReader
import com.example.geosnapper.Services.LocationService
import com.example.geosnapper.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    // MARKKERIEN PIIRTOA, NÄITÄKIN EHKÄ OMAAN LUOKKAAN
    private var drawDistance = 100
    private val posts: List<Post> by lazy {
        PostsReader(this).read()
    }
    private var setMapOnUserLocation = true     // PURKKAVIRITYKSEN TIETO FUNKTIOLLE JOLLA KARTTA PÄIVITETÄÄN KUN SAADAAN KÄYTTÄJÄN SIJAINTI
    private var currentLocation: LatLng = LatLng(65.0, 25.5)
    // LOKAATIOHOMMELEITA, EI MITÄÄN HAJUA MIKS NOI LUPIEN HAUT TOIMII TOLLEE MUT NE NYT TOIMII ENKÄ ENÄÄ USKALLA NIIHIN KOSKEE
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
            }
        }
    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED) {
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

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        checkLocationPermission()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // TÄSSÄ ON NAPIT JOITA VOI KÄYTTÄÄ VALIKKOJEN YMS AVAAMISEEN
        binding.buttonTest1.setOnClickListener {
            //val intent = Intent(this, MapActivity::class.java)
            //startActivity(intent);
        }
        binding.buttonTest2.setOnClickListener {
            //val intent = Intent(this, MapActivity::class.java)
            //startActivity(intent);
        }
    }

    fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                    //val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    //startActivity(intent)
                    locationPermissions.launch(
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
            }
            else {
                startService(service)
            }
        }
    }

    private fun updateMap() {
        if (setMapOnUserLocation) {
            placeMarkerOnMap(currentLocation, "You")
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
            setMapOnUserLocation = false
        }
        addMarkers()
    }

    // EKA VEDOS MARKKERIEN TULOSTUKSESTA MÄÄRÄTYLLÄ ETÄISYYDELLÄ. NÄITÄKIN VOIS EHKÄ SIVOTA OMAAN LUOKKAAN
    private fun addMarkers() {
        posts.forEach { post ->
            //if (calculateDistance(currentLocation, post.coordinates) < drawDistance) {
                placeMarkerOnMap(post.coordinates, post.title)
            //}
        }
    }

    private fun placeMarkerOnMap(coordinates: LatLng, title: String) {
        map.addMarker(MarkerOptions().position(coordinates).title(title))
    }

    // ETÄISYYSLASKURI VERSIO 1
    private fun calculateDistanceInKm(uLocation: Location, pLocation: LatLng): Double {
        fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }
        fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }
        val theta = uLocation.longitude - pLocation.longitude
        var dist = Math.sin(deg2rad(uLocation.latitude)) * Math.sin(deg2rad(pLocation.latitude)) + Math.cos(deg2rad(uLocation.latitude)) * Math.cos(deg2rad(pLocation.latitude)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }
    // ETÄISYYSLASKURI VERSIO 2. EI TOIMI TOLLEEN VAATII HIOMISTA
    private fun calculateDistance(uLocation: Location, pLocation: LatLng): Double {
        val locationP = Location("post")
        locationP.latitude = pLocation.latitude
        locationP.longitude = pLocation.longitude
        return uLocation.distanceTo(locationP).toDouble()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }

    override fun onMarkerClick(p0: Marker) = false

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
        currentLocation = LatLng(locationEvent.latitude!!, locationEvent.longitude!!)
        updateMap()
    }
}