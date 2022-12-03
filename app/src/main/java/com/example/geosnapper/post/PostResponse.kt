package com.example.geosnapper.post

import com.google.android.gms.maps.model.LatLng

data class PostResponse (                   // GOOGLEN MALLIN MUKAAN TEHTY. VARMAAN GEODATA KOHTAA PITÄIS PÄIVITTÄÄ
        val postId: String,
        val created: String,
        val geoData: Geometry,
        val type: String,
        val mediaLink: String,
        val message: String,
        val tier: Int,
        val uid: String,
    ) {
        data class Geometry(
            val location: GeometryLocation
        )
        data class GeometryLocation(
            val lat: Double,
            val lng: Double
        )
    }

    fun PostResponse.toPost(): Post = Post(
        postId = postId,
        created = created,
        coordinates = LatLng(geoData.location.lat, geoData.location.lng),
        type = type,
        mediaLink = mediaLink,
        message = message,
        tier = tier,
        userID = uid
    )