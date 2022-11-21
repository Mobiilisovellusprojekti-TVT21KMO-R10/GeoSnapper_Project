package com.example.geosnapper

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class MessageData(val message: String,
                       val mediaLink: String,
                       val created: Timestamp,
                       val geoData: String,
                       val tier: Int,
                       val uid: String)


