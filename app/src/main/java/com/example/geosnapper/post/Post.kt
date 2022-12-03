package com.example.geosnapper.post

import com.google.android.gms.maps.model.LatLng

    // TÄTÄ TARVII PÄIVITELLÄ KUNHAN SELVIÄÄ MITÄ TIETOJA POSTAUS SISÄLTÄÄ
data class Post (
        val postId: String,
        val created: String,
        val coordinates: LatLng,
        val type: String,
        val mediaLink: String,
        val message: String,
        val tier: Int,
        val userID: String,
    )