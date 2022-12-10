package com.example.geosnapper.dataHandling

import android.content.ContentValues
import android.util.Log
import com.example.geosnapper.MessageData
import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await



class Database {
    private val db = Firebase.firestore
    private val gson = Gson()
    private val MESSAGE_DATA = "messageData"
    private val USER_DATA = "userData"


    fun addMessage(msgData: MessageData) {
        db.collection(MESSAGE_DATA)
            .add(msgData)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapchot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    // kun ei osaa niin jälki on tän näköistä :DDD pääasia kuitenkin kai et toimii
    suspend fun getAllMessages(): List<Post> {
        val messages = ArrayList<Post>()
        db.collection(MESSAGE_DATA)
            .get().await().forEach {
                val values: MutableList<String> = mutableListOf()
                it.data.values.forEach {
                    values.add(it.toString())
                }
                val post: Post = Post(
                    gson.toJson(it.id),
                    values[2],
                    editLocationData(values[4]), //koordinaatit
                    "message",
                    values[5],
                    values[3],
                    values[1].toInt(),
                    values[0],
                )
                messages.add(post)
            }
        return messages
    }

    private fun editLocationData(data: String): LatLng {
        val latitudeString = data
            .replaceBefore('=', "")
            .replaceAfter(',', "")
            .trim('=', ',')
        val latitude= latitudeString.toDouble()
        val longitudeString = data
            .replaceBeforeLast('=', "")
            .trim('=', '}')
        val longitude = longitudeString.toDouble()
        return LatLng(latitude,longitude)
    }
}
