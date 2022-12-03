package com.example.geosnapper.marker

import com.example.geosnapper.LocalStorage

object MarkerConstants {     // NIIN KUIN NÄKYY NÄÄ ON VAAN HAHMOTTELUVAIHEESSA

    const val TIER1_LIFETIME = 1
    const val TIER2_LIFETIME = 1
    const val TIER3_LIFETIME = 1
    const val TIER1_VIEWDISTANCE = 15000
    const val TIER2_VIEWDISTANCE = 10000
    val TIER3_VIEWDISTANCE = getMarkerViewDistanceFromMemory()
    const val TIER3_OPENDISTANCE = 1

    // HAETAAN ETÄISYYSARVO PUHELIMEN MUISTISTA JOS MUISTIIN ON ASETETTU ARVO, MUUTEN VAKIO 5000
    private fun getMarkerViewDistanceFromMemory(): Int {
        var distance = 5000
        if (LocalStorage.getViewDistance() != 0) distance = LocalStorage.getViewDistance()
        return distance
    }
}
