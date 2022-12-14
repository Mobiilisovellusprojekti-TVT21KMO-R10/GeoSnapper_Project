package com.example.geosnapper.marker

import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class PostToMarkerClass {

    fun Post.toMarkerClass(): MarkerClass = MarkerClass(
        coordinates = coordinates,
        tier = tier,
        type = type,
        postId = postId,
        expires = setExpires(tier),
        icon = iconSelector(tier, type)
    )

    private fun setExpires(tier: Int): String {
        /*
        val expires = when (tier) {
            //1 -> created.toDateTaiJotainSinnep√§in + MarkerConstants.TIER1_LIFETIME
            //2 -> created.toDateTaiJotainSinnep√§in + MarkerConstants.TIER2_LIFETIME
            //else -> created.toDateTaiJotainSinnep√§in + MarkerConstants.TIER3_LIFETIME
        }
        return expires
        */
        return "the post lasts forever"
    }

    fun iconSelector(tier: Int, type: String): BitmapDescriptor {
        val icon =  when (tier) {
            1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            2 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            3 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            else -> BitmapDescriptorFactory.defaultMarker()
        }
        return icon
    }

    fun listHandler(posts: List<Post>): List<MarkerClass> {
        return posts.map {
            it.toMarkerClass()
        }
    }



}