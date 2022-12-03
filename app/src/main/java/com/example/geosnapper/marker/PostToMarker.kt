package com.example.geosnapper.marker

import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class PostToMarker {

    fun Post.toMarker(): Marker = Marker(
        coordinates = coordinates,
        tier = tier,
        type = type,
        postId = postId,
        expires = setExpires(tier),
        icon = iconSelector(tier, type)
    )

    private fun setExpires(tier: Int): String {
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

    // TÄTÄ PITÄÄ VIELÄ VIILATA KUNHAN JAKSAA. NYT VAAN TÄMMÖNEN HAHMOTELMA
    fun iconSelector(tier: Int, type: String): BitmapDescriptor {
        val icon =  when (tier) {
            1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            2 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            3 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            else -> BitmapDescriptorFactory.defaultMarker()
        }
        return icon
    }

    fun listHandler(posts: List<Post>): List<Marker> {
        return posts.map {
            it.toMarker()
        }
    }



}