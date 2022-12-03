package com.example.geosnapper

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geosnapper.events.LocationEvent
import com.example.geosnapper.marker.MarkerConstants
import com.example.geosnapper.marker.PostToMarker
import com.example.geosnapper.post.Post
import com.example.geosnapper.post.PostsReader
import com.example.geosnapper.services.LocationService
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
import com.google.firebase.auth.FirebaseAuth
import java.util.Random

class MapActivity : AppCompatActivity(),
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnInfoWindowLongClickListener {

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
            val intent = Intent(this, MainActivity::class.java)     // LOG OUTILLE OIS EHKÄ LOOGISEMPI JA PAREMPI PAIKKA JOSSAIN ASETUKSISSA, EIHÄN ME HALUTA ETTÄ KÄYTTÄJÄT NOIN HELPOSTI SOVELLUKSEN KÄYTÖN LOPETTAA:)
            intent.putExtra("userId", "null")
            firebaseAuth.signOut()
            LocalStorage.initialize()   // PYYHITÄÄN LAITTEESEEN TALLENNETUT TIEDOT
            startActivity(intent)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowLongClickListener(this)
    }

    private fun updateMap(currentLocation: LatLng?) {
        if (currentLocation != null) {
            if (setMapOnUserLocation) {
                setMapOnUserLocation = false
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                userMarker = map.addMarker(MarkerOptions().position(currentLocation).title("Happy GeoSnapping"))!!
                userMarker.tag = "user"
                addMarkers()
            }
            else {
                userMarker.position = currentLocation
                markersOnMap.map {
                    it.isVisible = calculateViewDistance(it.position, it.snippet!!.toInt())
                }
            }
        }
    }

    private fun addMarkers() {
        val markers = PostToMarker().listHandler(posts)
        markers.forEach { marker ->
            val post = posts.find {it.postId == marker.postId}
            if (post != null) {
                placeMarkerOnMap(marker, post)
            }
        }
    }

    private fun placeMarkerOnMap(marker: com.example.geosnapper.marker.Marker, post: Post) {
        val distance = calculateDistanceInMeters(marker.coordinates).toString()             // TÄÄ ON TESTAILUA
        val markerBuilder = map.addMarker(MarkerOptions()
            .position(marker.coordinates)
            .title(post.message + ", Etäisyys käyttäjästä: " + distance + " m")       // TESTAILUA
            .icon(marker.icon)
            .snippet(marker.tier.toString())
            .visible(calculateViewDistance(marker.coordinates, marker.tier))
        )
        markerBuilder!!.tag = post.postId
        markersOnMap.add(markerBuilder)
    }

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

    private fun calculateViewDistance(post: LatLng, tier: Int): Boolean {
        val results = calculateDistanceInMeters(post)
        val viewDistance = when (tier) {
            1 -> MarkerConstants.TIER1_VIEWDISTANCE
            2 -> MarkerConstants.TIER2_VIEWDISTANCE
            else -> MarkerConstants.TIER3_VIEWDISTANCE
        }
        return results < viewDistance
    }

    // TÄÄ ON TULEVAA MARKKERIEN / POSTIEN AVAAMISTA VARTEN. VOI OLLA TURHAKIN
    override fun onMarkerClick(marker : Marker): Boolean {
        selectedMarker = marker
        // OLI GOOGLEN ESIMERKEISSÄ TOMMONEN HAUSKA POMPPUANIMAATIO JA VÄRINVAIHTO NIIN LAITOIN NE TOHON USER MARKKERIIN
        if (marker.tag == "user") {
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val duration = 1500

            val interpolator = BounceInterpolator()

            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = Math.max(
                        1 - interpolator.getInterpolation(elapsed.toFloat() / duration), 0f)
                    marker.setAnchor(0.5f, 1.0f + 2 * t)

                    if (t > 0.0) {
                        handler.postDelayed(this, 16)
                    }
                }
            })
            val random = Random()
            marker.apply {
                setIcon(BitmapDescriptorFactory.defaultMarker(random.nextFloat() * 360))
            }
        }
        return false
    }

    // AJATUS OLISI AVATA VIESTIT EHTOJEN TÄYTTYESSÄ MARKERIN INFORUUTUA KLIKKAAMALLA
    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, "Tähän tulee viestin avausominaisuus", Toast.LENGTH_LONG).show()
    }

    // TÄHÄN SIT OMAN VIESTIN MUOKKAUS POISTO ETC
    override fun onInfoWindowLongClick(p0: Marker) {
        Toast.makeText(this, "Ja tästä mahdollisesti viestiä muokkaamaan", Toast.LENGTH_LONG).show()
    }
    /*
    override fun getInfoWindow(marker: Marker): View? {

    }
    */
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

    // KOITA SAADA TÄÄ TOIMIIN
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startService(service)
                locationPermission = true
            }
        //}
    }
}

