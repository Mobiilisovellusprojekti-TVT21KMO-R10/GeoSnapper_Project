package com.example.geosnapper

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.*
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
import java.io.Serializable

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
            intent.putExtra("userId", passedValue)
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


    // KARTAN PÄIVITYSTÄ
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {

        // MAHD. TÄHÄN MAPFRGAMENTIN HIDEEMINEN JOS EI OO LOKAATIOTIETOO SALLITTU TAI SITTEN JONNEKKIN MUUALLE

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowLongClickListener(this)
        map.setInfoWindowAdapter(CustomInfoWindowAdapter())
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
                    it.isVisible = checkViewDistance(it.position, it.snippet!!.toInt())     // MUISTA PÄIVITTÄÄ TÄÄ
                }
            }
        }
    }

    public final fun getLocation(): LatLng {
        return userLocation
    }


    // MARKKERIJUTTUJA
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
            .visible(checkViewDistance(marker.coordinates, marker.tier))
        )
        markerBuilder!!.tag = post
        markersOnMap.add(markerBuilder)
    }

    private fun calculateDistanceInMeters(coordinates: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            coordinates.latitude,
            coordinates.longitude,
            results
        )
        return results[0]
    }

    private fun checkViewDistance(post: LatLng, tier: Int): Boolean {
        val results = calculateDistanceInMeters(post)
        val viewDistance = when (tier) {
            1 -> MarkerConstants.TIER1_VIEWDISTANCE
            2 -> MarkerConstants.TIER2_VIEWDISTANCE
            else -> MarkerConstants.TIER3_VIEWDISTANCE
        }
        return results < viewDistance
    }

    private fun checkOpenDistance(marker: Marker): Boolean {
        val distance = calculateDistanceInMeters(marker.position)
        val result = when (marker.snippet) {
            "1" -> true
            "2" -> true
            else -> distance < MarkerConstants.TIER3_OPENDISTANCE
        }
        return result
    }
    // OLI GOOGLEN ESIMERKEISSÄ TOMMONEN HAUSKA POMPPUANIMAATIO JA VÄRINVAIHTO NIIN LAITOIN NE TOHON USER MARKKERIIN
    override fun onMarkerClick(marker : Marker): Boolean {
        selectedMarker = marker
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
    // MARKERIEN CUSTOM CALLOUT
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        private val infoWindow: View = layoutInflater.inflate(R.layout.infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
            if (marker.tag == "user") {
                return null
            }
            render(marker, infoWindow)
            return infoWindow
        }

        override fun getInfoContents(marker: Marker): View? {   // JOSTAIN SYYSTÄ VAATII TÄN
            return null
        }

        private fun render(marker: Marker, view: View) {
            val post = marker.tag as Post

            view.findViewById<ImageView>(R.id.userAvatar).setImageResource(R.drawable.test_avatar)

            val userName: String = when (post.userID) {         // EI OO USERNAMEE NIIN TÄSSÄ ON VAAN NÄÄ HARDKOODATTUNA
                "i69kfXgRYlR3EzhE4KHe9plDeVd2" -> "The Big E"
                else -> "setäSomuli"
            }
            val userNameUi = view.findViewById<TextView>(R.id.userName)
            userNameUi.text = userName

            val title: String = post.type
            val titleUi = view.findViewById<TextView>(R.id.title)
            titleUi.text = SpannableString(title).apply {
                setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
            }

            val description1Ui = view.findViewById<TextView>(R.id.description1)
            val desc1: String
            if (checkOpenDistance(marker) || post.userID == LocalStorage.getUserId()) {
                desc1 = "Tap to open post"
            } else {
                desc1 = "You have to get closer to open"
            }
            description1Ui.text = desc1

            val description2Ui = view.findViewById<TextView>(R.id.description2)
            val desc2: String
            if (post.userID == LocalStorage.getUserId()) {
                desc2 = "Press long to edit"
            }
            else {
                desc2 = ""
            }
            description2Ui.text = desc2
        }
    }
    // AJATUS OLISI AVATA VIESTIT EHTOJEN TÄYTTYESSÄ MARKERIN INFORUUTUA KLIKKAAMALLA
    override fun onInfoWindowClick(marker: Marker) {
        if (marker.tag != "user") {
            val post = marker.tag as Post
//            if (post.userID == LocalStorage.getUserId() || checkOpenDistance(marker)) {
//
//            }
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.layout_popup, null)

            // step 2
            val wid = LinearLayout.LayoutParams.WRAP_CONTENT
            val high = LinearLayout.LayoutParams.WRAP_CONTENT
            val focus= true
            val popupWindow = PopupWindow(popupView, wid, high, focus)

            // step 3
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

            val popupText = popupView.findViewById<TextView>(R.id.popup_window_text)
            popupText.text = post.message

        }
    }
    // TÄHÄN SIT OMAN VIESTIN MUOKKAUS POISTO ETC
    override fun onInfoWindowLongClick(marker: Marker) {
        Log.d("Map Activity", "tultiin onInfoWindowLongClickiin")
        if (marker.tag != "user") {
            val post = marker.tag as Post
            if (post.userID == LocalStorage.getUserId()) {
                val intent = Intent(this, ViewMessageActivity::class.java)
                intent.putExtra("post", post)
                startActivity(intent);
            }
        }
    }



    // SIJAINTITIEDON LUVANHAKUA
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                startService(service)                   // TÄN VOIS SIIRTÄÄ
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startService(service)
            locationPermission = true
        }
    }



    // EVENTBUSS / SERVICEJUTTUJA
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