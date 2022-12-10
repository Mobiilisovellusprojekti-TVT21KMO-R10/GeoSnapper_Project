package com.example.geosnapper.post

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class PostResponse (
        val postId: String,
        val uid: String,
        val tier: Int,
        val created: String,
        val message: String,
        val geoData: Geometry,
        val mediaLink: String,
        val type: String,
    ) {

    data class Geometry(
            val location: GeometryLocation
        )
        data class GeometryLocation(
            val lat: Double = 0.0,
            val lng: Double = 0.0
        )

    }


    fun PostResponse.toPost(): Post = Post(
        postId = postId,
        created = created.toString(),
        coordinates = LatLng(geoData.location.lat, geoData.location.lng),
        type = type,
        mediaLink = mediaLink,
        message = message,
        tier = tier,
        userID = uid
    )