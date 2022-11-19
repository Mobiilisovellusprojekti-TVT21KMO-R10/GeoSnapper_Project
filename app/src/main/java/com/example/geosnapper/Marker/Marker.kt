package com.example.geosnapper.Marker

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Marker (
        val coordinates: LatLng,
        val tier: Int,
        val type: String,
        val postId: String,
        val expires: String
    )



    /*
    CLUSTEROINTI VARMAAN KANNATTAA OTTAA KÄYTTÖÖN, MUTTA EI EHKÄ NYT ALUN TESTIVAIHEESSA
    : ClusterItem {
        override fun getPosition(): LatLng =
            latLng
        ...
    }
    */
