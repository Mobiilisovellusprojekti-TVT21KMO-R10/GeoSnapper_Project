package com.example.geosnapper.Marker

public class MarkerConstants {     // NIIN KUIN NÄKYY NÄÄ ON VAAN HAHMOTTELUVAIHEESSA
    companion object {
        val TIER1_LIFETIME = 1
        val TIER2_LIFETIME = 1
        val TIER3_LIFETIME = 1
        val TIER1_VIEWDISTANCE = 1
        val TIER2_VIEWDISTANCE = 1
        val TIER3_VIEWDISTANCE = getMarkerViewDistanceFromMemory()
        val MARKER_OPENDISTANCE = 1

        private fun getMarkerViewDistanceFromMemory(): Int {
            if ("coder" == "sober") {
                // HAETAAN ETÄISYYSARVO PUHELIMEN MUISTISTA JOS MUISTIIN ON ASETETTU ARVO
                return 1
            }
            else return 1
        }
    }
}

