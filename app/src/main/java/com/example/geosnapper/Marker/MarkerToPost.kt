package com.example.geosnapper.Marker

import com.example.geosnapper.Post.Post
import com.google.android.gms.maps.model.LatLng

class MarkerToPost {

    fun Post.toMarker(): Marker = Marker(
        coordinates = coordinates,
        tier = tier,
        type = type,
        postId = postId,
        expires = setExpires()
    )

    private fun setExpires(): String {
        // TÄHÄN FUNKTIOON VOIS IMPLEMENTOIDA MYÖS SEN ELINAJAN KASVATTAMISEN PEUKUTUSTEN PERUSTEELLA, JOS ME OTETAAN NE PEUKALOINNIT OHJELMAAN MUKAAN
        /*
        val expires = when (tier) {
            //1 -> created.toDateTaiJotainSinnepäin + MarkerConstants.TIER1_LIFETIME
            //2 -> created.toDateTaiJotainSinnepäin + MarkerConstants.TIER2_LIFETIME
            //else -> created.toDateTaiJotainSinnepäin + MarkerConstants.TIER3_LIFETIME
        }
        return expires
        */
        return "the post lasts forever"
    }

    fun listHandler(posts: List<Post>): List<Marker> {
        return posts.map {
            it.toMarker()
        }
    }
}