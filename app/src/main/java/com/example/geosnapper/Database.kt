package com.example.geosnapper

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class Database {
    private val db = Firebase.firestore

    fun addMessage(msgData: MessageData) {
        db.collection("messageData")
            .add(msgData)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapchot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }


}