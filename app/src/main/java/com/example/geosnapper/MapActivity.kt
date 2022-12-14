package com.example.geosnapper

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geosnapper.dataHandling.Database
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.locationService.LocationEvent
import com.example.geosnapper.marker.PostToMarkerClass
import com.example.geosnapper.locationService.LocationService
import com.example.geosnapper.databinding.ActivityMapBinding
import com.example.geosnapper.locationService.LocationPermissions
import com.example.geosnapper.marker.MarkerClass
import com.example.geosnapper.marker.MarkerRender
import com.example.geosnapper.post.Post
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MapActivity : AppCompatActivity(),
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnInfoWindowLongClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    private lateinit var firebaseAuth: FirebaseAuth
    private var setMapOnUserLocation = true
    private var locationPermission = false
    private var userLocation = LatLng(0.0, 0.0)
    private lateinit var userMarker: Marker
    private var markersOnMap = ArrayList<Marker>()
    private var posts: List<Post>? = null
    private val database = Database()
    private val locationPermissions = LocationPermissions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        locationPermission = locationPermissions.checkPermissions()
        if (!locationPermission) locationPermissions.requestPermissions() else startService(service)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // TÄSSÄ ON NAPIT JOITA VOI KÄYTTÄÄ VALIKKOJEN YMS AVAAMISEEN
        binding.buttonTest1.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            intent.putExtra("lat", userLocation.latitude.toString())
            intent.putExtra("lng", userLocation.longitude.toString())
            startActivity(intent)
        }
        binding.buttonTest2.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogout.setOnClickListener {
            //firebaseAuth.signOut()              // LOG OUTILLE OIS EHKÄ LOOGISEMPI JA PAREMPI PAIKKA JOSSAIN ASETUKSISSA, EIHÄN ME HALUTA ETTÄ KÄYTTÄJÄT NOIN HELPOSTI SOVELLUKSEN KÄYTÖN LOPETTAA:)
            //LocalStorage.initialize()           // PYYHITÄÄN LAITTEESEEN TALLENNETUT TIEDOT
            //finish()
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
                //getPostsFromDatabase()
                database.getAllMessages2()
                //addMarkers()
            }
            else {
                userMarker.position = currentLocation
                markersOnMap.map {
                    it.isVisible = MarkerRender.checkViewDistance(userLocation, it.position, it.snippet!!.toInt())     // MUISTA PÄIVITTÄÄ TÄÄ
                }
            }
        }
    }

    private fun getPostsFromDatabase() = runBlocking {
        launch {
            Log.d("datesti", "Ja terse")
            posts = database.getAllMessages()
        }
    }



    // MARKKERIJUTTUJA
    private fun addMarkers() {
        val markers = posts?.let { PostToMarkerClass().listHandler(it) }
        markers?.forEach { marker ->
            val post = posts?.find {it.postId == marker.postId}
            if (post != null) {
                placeMarkerOnMap(marker, post)
            }
        }
    }

    private fun placeMarkerOnMap(marker: MarkerClass, post: Post) {
        val markerBuilder = map.addMarker(MarkerOptions()
            .position(marker.coordinates)
            .icon(marker.icon)
            .snippet(marker.tier.toString())
            .visible(MarkerRender.checkViewDistance(userLocation, marker.coordinates, marker.tier))
        )
        markerBuilder?.tag = post
        if (markerBuilder != null) {
            markersOnMap.add(markerBuilder)
        }
    }

    private fun removeMarker(marker: Marker) {
        val post = marker.tag as Post
        if (database.deleteMessage(post.postId)) {
            marker.remove()
            markersOnMap.find{it == marker}?.remove()
            Toast.makeText(this, "Post deteted successfully", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "Post deletion failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        if (marker.tag == "user") {
            MarkerRender.jumpAnimation(marker)
            MarkerRender.changeRandomColor(marker)
        }
        return false
    }

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        private val infoWindow: View = layoutInflater.inflate(R.layout.infowindow, null)
        override fun getInfoWindow(marker: Marker): View? {
            if (marker.tag == "user") {
                return null
            }
            MarkerRender.renderInfoWindow(userLocation, marker, infoWindow)
            return infoWindow
        }
        override fun getInfoContents(marker: Marker): View? {   // JOSTAIN SYYSTÄ VAATII TÄN
            return null
        }
    }
    // AJATUS OLISI AVATA VIESTIT EHTOJEN TÄYTTYESSÄ MARKERIN INFORUUTUA KLIKKAAMALLA
    override fun onInfoWindowClick(marker: Marker) {
        if (marker.tag != "user") {
            val post = marker.tag as Post
            //if (post.userID == LocalStorage.getUserId() || checkOpenDistance(marker)) {

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.layout_popup, null)

                // step 2
                val wid = LinearLayout.LayoutParams.WRAP_CONTENT
                val high = LinearLayout.LayoutParams.WRAP_CONTENT
                val focus = true
                val popupWindow = PopupWindow(popupView, wid, high, focus)

                // step 3
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

                val popupText = popupView.findViewById<TextView>(R.id.popup_window_text)
                popupText.text = post.message
            //}
        }
        marker.hideInfoWindow()
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
        marker.hideInfoWindow()
    }



    // EVENTBUSS / SERVICEJUTTUJA
    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startService(service)
            locationPermission = true
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        userLocation = LatLng(locationEvent.latitude!!, locationEvent.longitude!!)
        updateMap(userLocation)
    }

    @Subscribe
    fun receivePostsEvent(PostsEvent: List<Post>) {
        posts = PostsEvent
        addMarkers()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }
}