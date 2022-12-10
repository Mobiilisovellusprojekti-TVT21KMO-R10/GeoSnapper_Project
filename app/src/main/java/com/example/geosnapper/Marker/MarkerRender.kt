package com.example.geosnapper.marker

object MarkerRender {
    /*
    fun calculateDistanceInMeters(coordinates: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            MapActivity().getLocation().latitude,
            MapActivity().getLocation().longitude,
            coordinates.latitude,
            coordinates.longitude,
            results
        )
        return results[0]
    }

    fun checkViewDistance(coordinates: LatLng, tier: Int): Boolean {
        val distance = calculateDistanceInMeters(coordinates)
        val viewDistance = when (tier) {
            1 -> MarkerConstants.TIER1_VIEWDISTANCE
            2 -> MarkerConstants.TIER2_VIEWDISTANCE
            else -> MarkerConstants.TIER3_VIEWDISTANCE
        }
        return distance < viewDistance
    }

    fun checkOpenDistance(marker: Marker): Boolean {
        val distance = calculateDistanceInMeters(marker.position)
        val result = when (marker.snippet) {
            "1" -> true
            "2" -> true
            else -> distance < MarkerConstants.TIER3_OPENDISTANCE
        }
        return result
    }

    private fun renderInfoWindow(marker: Marker, view: View) {
        val post = marker.tag as Post

        view.findViewById<ImageView>(R.id.userAvatar).setImageResource(R.drawable.test_avatar)

        val userName: String = when (post.userID) {         // EI OO USERNAMEE NIIN TÄSSÄ ON VAAN NÄÄ HARDKOODATTUNA
            "i69kfXgRYlR3EzhE4KHe9plDeVd2" -> "The Big E"
            else -> "setäSomuli"
        }
        val userNameUi = view.findViewById<TextView>(R.id.userName)
        userNameUi.text = userName

        val title: String = post.type
        val titleUi = view.findViewById<TextView>(R.id.title)
        titleUi.text = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
        }

        val description1Ui = view.findViewById<TextView>(R.id.description1)
        val desc1: String
        if (MarkerRender.checkOpenDistance(marker) || post.userID == LocalStorage.getUserId()) {
            desc1 = "Tap to open post"
        } else {
            desc1 = "You have to get closer to open"
        }
        description1Ui.text = desc1

        val description2Ui = view.findViewById<TextView>(R.id.description2)
        val desc2: String
        if (post.userID == LocalStorage.getUserId()) {
            desc2 = "Press long to edit"
        }
        else {
            desc2 = ""
        }
        description2Ui.text = desc2
    }

     */
}