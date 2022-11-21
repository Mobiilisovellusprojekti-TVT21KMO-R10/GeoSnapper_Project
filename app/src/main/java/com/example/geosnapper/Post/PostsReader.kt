package com.example.geosnapper.Post

import android.content.Context
import com.example.geosnapper.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

class PostsReader(private val context: Context) {

    private val gson = Gson()
    private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.posts)      // HAETAAN TESTIVAIHEESSA POSTAUKSET PAIKALLISESTA POSTS.JSON TIEDOSTOSTA

    fun read(): List<Post> {
        val itemType = object: TypeToken<List<PostResponse>>() {}.type
        val reader = InputStreamReader(inputStream)
        return gson.fromJson<List<PostResponse>>(reader, itemType).map {
            it.toPost()
        }
    }
}