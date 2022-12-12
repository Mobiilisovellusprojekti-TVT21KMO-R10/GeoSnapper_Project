package com.example.geosnapper.marker

import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.SystemClock
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.example.geosnapper.Marker.MarkerConstants1
import com.example.geosnapper.R
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.*

object MarkerRender {

   fun calculateDistanceInMeters(userLocation: LatLng, postCoordinates: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            postCoordinates.latitude,
            postCoordinates.longitude,
            results
        )
        return results[0]
    }

    fun checkViewDistance(userLocation: LatLng ,postCoordinates: LatLng, tier: Int): Boolean {
        val distance = calculateDistanceInMeters(userLocation, postCoordinates)
        val viewDistance = when (tier) {
            1 -> MarkerConstants1.TIER1_VIEWDISTANCE
            2 -> MarkerConstants1.TIER2_VIEWDISTANCE
            else -> MarkerConstants1.TIER3_VIEWDISTANCE
        }
        return distance < viewDistance
    }

    fun checkOpenDistance(userLocation: LatLng, marker: Marker): Boolean {
        val distance = calculateDistanceInMeters(userLocation, marker.position)
        val result = when (marker.snippet) {
            "1" -> true
            "2" -> true
            else -> distance < MarkerConstants1.TIER3_OPENDISTANCE
        }
        return result
    }

    fun renderInfoWindow(userLocation: LatLng, marker: Marker, view: View) {
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
        if (checkOpenDistance(userLocation, marker) || post.userID == LocalStorage.getUserId()) {
            desc1 = "Tap to open post"
        }
        else {
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

    fun jumpAnimation(marker: Marker) {
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
    }

    fun changeRandomColor(marker: Marker) {
        val random = Random()
        marker.apply {
            setIcon(BitmapDescriptorFactory.defaultMarker(random.nextFloat() * 360))
        }
    }
}