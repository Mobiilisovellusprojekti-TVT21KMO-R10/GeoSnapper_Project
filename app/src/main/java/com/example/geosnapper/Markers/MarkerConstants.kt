package com.example.geosnapper.Markers

import com.example.geosnapper.dataHandling.LocalStorage

object MarkerConstants {

    val TIER1_LIFETIME = 1
    val TIER2_LIFETIME = 1
    val TIER3_LIFETIME = 1
    val TIER1_VIEWDISTANCE = 100000
    val TIER2_VIEWDISTANCE = 50000
    val TIER3_VIEWDISTANCE = getMarkerViewDistanceFromMemory()
    val TIER3_OPENDISTANCE = 1500


    private fun getMarkerViewDistanceFromMemory(): Int {
        var distance = 15000
        if (LocalStorage.getViewDistance() != 0) distance = LocalStorage.getViewDistance()
        return distance
    }
}
