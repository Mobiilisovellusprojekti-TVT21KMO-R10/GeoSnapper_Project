package com.example.geosnapper.Post

import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.clustering.ClusterItem

    // TÄTÄ TARVII PÄIVITELLÄ KUNHAN SELVIÄÄ MITÄ TIETOJA POSTAUS SISÄLTÄÄ
data class Post (
    val coordinates: LatLng,
    val title: String
)
    /* CLUSTEROINTI VARMAAN KANNATTA OTTAA KÄYTTÖÖN, MUTTA EI NYT ALUN TESTIVAIHEESSA
    : ClusterItem {
    override fun getPosition(): LatLng =
        latLng

    override fun getTitle(): String =
        name

    override fun getSnippet(): String =
        address
}

     */