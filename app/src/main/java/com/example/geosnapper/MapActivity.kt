package com.example.geosnapper

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
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



class MapActivity : AppCompatActivity(),
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var service: Intent
    private var setMapOnUserLocation = true
    private var locationPermission = false
    private var userLocation = LatLng(0.0, 0.0)
    private lateinit var userMarker: Marker
    private var markersOnMap = ArrayList<Marker>()
    private var posts: List<Post>? = null
    private val database = Database()
    private val locationPermissions = LocationPermissions(this)
    private lateinit var locationDialog: ProgressDialog
    private val popupRender = EditMessagePopupRender(this)
    private val MarkerRender = MarkerRender(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = Intent(this, LocationService::class.java)
        locationPermission = locationPermissions.checkPermissions()
        if (!locationPermission) locationPermissions.requestPermissions() else startService(service)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


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
        getString(R.string.open_post)
    }



    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
        map.setInfoWindowAdapter(CustomInfoWindowAdapter())
        locationDialog = ProgressDialog(this)
        locationDialog.setTitle(getString(R.string.getting_location))
        locationDialog.setMessage(getString(R.string.please_wait))
        locationDialog.setCancelable(false)
        locationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        locationDialog.show()
    }

    private fun updateMap(currentLocation: LatLng?) {
        if (currentLocation != null) {
            if (setMapOnUserLocation) {
                locationDialog.dismiss()
                setMapOnUserLocation = false
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                userMarker = map.addMarker(MarkerOptions().position(currentLocation).title("Happy GeoSnapping"))!!
                userMarker.tag = "user"
                database.getAllMessages2()
            }
            else {
                userMarker.position = currentLocation
                markersOnMap.map {
                    it.isVisible = MarkerRender.checkViewDistance(userLocation, it.position, it.snippet!!.toInt())
                }
            }
        }
    }



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
        marker.remove()
        markersOnMap.find{it == marker}?.remove()
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
        override fun getInfoContents(marker: Marker): View? {
            return null
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (marker.tag != "user") {
            val post = marker.tag as Post
            if (post.userID == LocalStorage.getUserId() || MarkerRender.checkOpenDistance(userLocation, marker)) {
                val popupView: View = layoutInflater.inflate(R.layout.editmessage_popup, null)
                var ownPost = false
                if (post.userID == LocalStorage.getUserId()) {
                    ownPost = true
                }
                popupRender.render(popupView, marker, ownPost, ::removeMarker)
            }
        }
        marker.hideInfoWindow()
    }



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