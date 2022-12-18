package com.example.geosnapper.marker

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

data class MarkerClass (
        val coordinates: LatLng,
        val tier: Int,
        val type: String,
        val postId: String,
        val expires: String,
        val icon: BitmapDescriptor,
    )
