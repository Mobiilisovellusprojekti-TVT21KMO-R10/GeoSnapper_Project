package com.example.geosnapper.Post

import com.google.android.gms.maps.model.LatLng

data class PostResponse (
    val geometry: Geometry,
    val title: String,
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
    coordinates = LatLng(geometry.location.lat, geometry.location.lng),
    title = title
)