package com.example.geosnapper.Marker

import com.example.geosnapper.dataHandling.LocalStorage

object MarkerConstants {     // NIIN KUIN NÄKYY NÄÄ ON VAAN HAHMOTTELUVAIHEESSA

    val TIER1_LIFETIME = 1
    val TIER2_LIFETIME = 1
    val TIER3_LIFETIME = 1
    val TIER1_VIEWDISTANCE = 15000
    val TIER2_VIEWDISTANCE = 10000
    val TIER3_VIEWDISTANCE = getMarkerViewDistanceFromMemory()
    val TIER3_OPENDISTANCE = 1

    // HAETAAN ETÄISYYSARVO PUHELIMEN MUISTISTA JOS MUISTIIN ON ASETETTU ARVO, MUUTEN VAKIO 5000
    private fun getMarkerViewDistanceFromMemory(): Int {
        var distance = 5000
        if (LocalStorage.getViewDistance() != 0) distance = LocalStorage.getViewDistance()
        return distance
    }
}
