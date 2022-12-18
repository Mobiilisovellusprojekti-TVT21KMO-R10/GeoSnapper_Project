package com.example.geosnapper.dataHandling

import android.content.ContentValues
import android.util.Log
import android.widget.TextView
import com.example.geosnapper.MessageData
import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus


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
    fun getAllMessages2(){
        val messages = ArrayList<Post>()
        val collection = db.collection(MESSAGE_DATA)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val values: MutableList<String> = mutableListOf()
                    document.data.values.forEach {
                        values.add(it.toString())
                    }
                    val post: Post = Post(
                        gson.toJson(document.id),
                        values[document.data.keys.indexOf("created")],
                        editLocationData(values[document.data.keys.indexOf("geoData")]),
                        "message",
                        values[document.data.keys.indexOf("mediaLink")],
                        values[document.data.keys.indexOf("message")],
                        values[document.data.keys.indexOf("tier")].toInt(),
                        values[document.data.keys.indexOf("uid")],
                    )
                    messages.add(post)
                }
                EventBus.getDefault().post(messages)
            }
    }

    private fun editLocationData(data: String): LatLng {
        val latitudeString = data
            .replaceBefore('=', "")
            .replaceAfter(',', "")
            .trim('=', ',')
        val latitude = latitudeString.toDouble()
        val longitudeString = data
            .replaceBeforeLast('=', "")
            .trim('=', '}')
        val longitude = longitudeString.toDouble()
        return LatLng(latitude,longitude)
    }

    fun deleteMessage(postId: String): Boolean {
        val document = db.collection(MESSAGE_DATA).document(postId.trim('"'))
        return try {
            document.delete()
            true
        }
        catch (e: FirebaseFirestoreException) {
            false
        }
    }

    fun updatePostsOneValue(postId: String, key:String, value: Any): Boolean {
        val document = db.collection(MESSAGE_DATA).document(postId.trim('"'))
        return try {
            document.update(key, value)
            true
        }
        catch (e: FirebaseFirestoreException) {
            false
        }
    }
}
