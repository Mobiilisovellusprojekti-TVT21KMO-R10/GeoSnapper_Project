package com.example.geosnapper

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geosnapper.Post.PostsReader
import com.example.geosnapper.databinding.ActivityProfileBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

import java.util.*
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var messageDataList : MutableList<MessageData> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var email: String? = ""
        var uid: String? = ""

        val messageObject = MessageData("Juhuu",
            "",
            Timestamp(Date()),
            LatLng(27.0,64.0),
            1,
            "i69kfXgRYlR3EzhE4KHe9plDeVd2")

        val user = FirebaseAuth.getInstance().currentUser
        user?.let{
            email = user.email
            uid = user.uid
        }

        binding.collapsingToolbarProfileView.title = email


        Log.d("Profile Activity","uid = ${uid}" )
        binding.buttonShowMessages.setOnClickListener {
            fetchMessages(uid)
            val adapter = ArrayAdapter(this, R.layout.view_compound_message, messageDataList)
            val listView : ListView = findViewById(R.id.listViewProfile)
            listView.adapter = adapter
        }

        binding.buttonToSettings.setOnClickListener() {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun fetchMessages(uid: String?){
        val db = Firebase.firestore
        val collection = db.collection("messageData")

        collection.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val gson = Gson()

                    val messageData = gson.toJson(document.data)

                    val testData = gson.fromJson(messageData, MessageData::class.java)
                    val dataMessageObject = MessageData(testData.message, "", testData.created, testData.geoData, 1, uid)

                    Log.d("MESSAGEDATA", "$testData")

                    messageDataList.add(0, dataMessageObject)

                    //TODO: Koita saada printtaamaan listaan
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting message data", exception)
            }
    }

    fun showMessages(uid: String) {
        fetchMessages(uid)
        val adapter = ArrayAdapter(this, R.layout.view_compound_message, messageDataList)
    }
}

