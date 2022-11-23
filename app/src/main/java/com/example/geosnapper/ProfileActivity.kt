package com.example.geosnapper

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
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

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        val compoundMessageView = CompoundMessageView(this)

        setContentView(view)

        var email: String? = ""
        var uid: String? = ""

        fun fetchMessages(uid: String?){
            val db = Firebase.firestore

            val collection = db.collection("messageData")
            collection.whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val gson = Gson()

                        val messageData = gson.toJson(document.data)

                        val testData = gson.fromJson(messageData, MessageData::class.java)

                        Log.d("MESSAGEDATA", "$testData")

                        //val messageView = binding.clMessageInfoBase

                        compoundMessageView.message = testData.message
                        compoundMessageView.created = testData.created.toString()
                        compoundMessageView.geoData = testData.geoData.toString()

                        //TODO: Selvitä, miksi herjaa tätä: "java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first."

                        /*
                        binding.tvMessage.text = testData.message
                        binding.tvCreated.text = testData.created.toString()
                        binding.tvGeoData.text = testData.geoData.toString()
                        */

                        binding.linearLayoutProfileInner.addView(compoundMessageView)

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting message data", exception)
                }

        }

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

        val db = Database()

        //db.addMessage(messageObject)
        //fetchMessages(uid)


        binding.collapsingToolbarProfileView.title = email


        Log.d("Profile Activity","uid = ${uid}" )
        binding.buttonShowMessages.setOnClickListener {
            fetchMessages(uid)
        }

        binding.buttonToSettings.setOnClickListener() {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}

