package com.example.geosnapper.Marker

import android.content.Context
import com.example.geosnapper.LocalStorage

public class MarkerConstants (context: Context) {     // NIIN KUIN NÄKYY NÄÄ ON VAAN HAHMOTTELUVAIHEESSA
    companion object {
        val TIER1_LIFETIME = 1
        val TIER2_LIFETIME = 1
        val TIER3_LIFETIME = 1
        val TIER1_VIEWDISTANCE = 15000
        val TIER2_VIEWDISTANCE = 10000
        val TIER3_VIEWDISTANCE = getMarkerViewDistanceFromMemory()
        val MARKER_OPENDISTANCE = 1

        // HAETAAN ETÄISYYSARVO PUHELIMEN MUISTISTA JOS MUISTIIN ON ASETETTU ARVO, MUUTEN VAKIO 5000
        private fun getMarkerViewDistanceFromMemory(): Int {
            LocalStorage().getViewDistance()?.let { distance ->
                return distance
            }
            return 5000
        }
    }
}

