package com.example.geosnapper

import android.Manifest
import android.animation.ValueAnimator
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
import com.example.geosnapper.Marker.MarkerConstants
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.annotation.meta.When
import kotlin.properties.Delegates
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import java.lang.Math.abs

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    private lateinit var client: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var setMapOnUserLocation = true
    private lateinit var  selectedMarker: Marker
    private var locationPermission = false
    private var userLocation = LatLng(0.0, 0.0)
    private lateinit var animator: ValueAnimator
    private lateinit var userMarker: Marker
    private var markersOnMap = ArrayList<Marker>()

    private val posts: List<Post> by lazy {         // TÄÄ ON VÄLIAIKAINEN RATKAISU TESTAILUA VARTEN
        PostsReader(this).read()
    }

    // LOCATIONSERVICE VAATII TÄMMÖSET HIRVITYKSET
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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

        firebaseAuth = FirebaseAuth.getInstance()
        client = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        locationPermission = checkPermissions()
        if (!locationPermission) requestPermissions()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Käyttäjän USERID on tässä muuttujassa
        val passedValue = intent.getStringExtra("userId")

        // TÄSSÄ ON NAPIT JOITA VOI KÄYTTÄÄ VALIKKOJEN YMS AVAAMISEEN
        binding.buttonTest1.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }
        binding.buttonTest2.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userId", "null")
            firebaseAuth.signOut()
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
    }

    private fun updateMap(currentLocation: LatLng?) {
        if (currentLocation != null) {
            if (setMapOnUserLocation) {
                setMapOnUserLocation = false
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                userMarker = map.addMarker(MarkerOptions().position(currentLocation).title("You"))!!
                addMarkers()
            }
            else {
                userMarker.position = currentLocation
                markersOnMap.map {
                    it.isVisible = calculateViewDistance(it.position, it.snippet?.toInt())
                }
            }
        }
    }

    private fun addMarkers() {      // TESTIVAIHEESSA
        val markers = MarkerToPost().listHandler(posts)
        markers.forEach { marker ->
            val distance = calculateDistanceInMeters(marker.coordinates).toString()
            val post = posts.find {it.postId == marker.postId}
            placeMarkerOnMap(
                marker.coordinates,
                post!!.message + ", Etäisyys käyttäjästä: " + distance + " m",  // TÄÄ ON TESTAILUA
                marker.tier
            )
        }
    }

    private fun placeMarkerOnMap(coordinates: LatLng, title: String, tier: Int = 0) {
        // TÄN VOIS SIIRTÄÄ MARKER LUOKKAAN TAI OMAAN FUNKTIOON
        val icon =  when (tier) {
            1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            2 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            3 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            else -> BitmapDescriptorFactory.defaultMarker()
        }
        val marker = map.addMarker(MarkerOptions()
            .position(coordinates)
            .title(title)
            .icon(icon)
            .snippet(tier.toString())
            .visible(calculateViewDistance(coordinates, tier))
        )
        markersOnMap.add(marker!!)
    }

    // MARKKERIEN PIIRTOETÄISYYDEN LASKUN TESTAILUA VERSIO 3
    private fun calculateDistanceInMeters(post: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            post.latitude,
            post.longitude,
            results
        )
        return results[0]
    }

    private fun calculateViewDistance(post: LatLng, tier: Int?): Boolean {
        val results = calculateDistanceInMeters(post)
        val viewDistance = when (tier) {
            1 -> MarkerConstants.TIER1_VIEWDISTANCE
            2 -> MarkerConstants.TIER2_VIEWDISTANCE
            else -> MarkerConstants.TIER3_VIEWDISTANCE
        }
        return results < viewDistance
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
        return false
    }

    private fun requestPermissions() {
        Toast.makeText(this, "Please allow the app to use location data", Toast.LENGTH_LONG).show()
        locationPermissions.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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

