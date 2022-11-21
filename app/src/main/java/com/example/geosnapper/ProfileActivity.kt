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
import com.example.geosnapper.databinding.ActivityProfileBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.type.LatLng
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var name: String? = ""
        var email: String? = ""
        var uid: String? = ""
        var myMessages: List<Any>? = listOf()


        fun fetchMessages(uid: String?){
            val db = Firebase.firestore

            val collection = db.collection("messageData")
            collection.whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val gson = Gson()
                        val messageData = gson.toJson(document.data)

                        val messageView = TextView(this)
                        messageView.text = messageData

                        binding.linearLayoutProfileInner.addView(messageView)

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting message data", exception)
                }

        }

        val messageObject = MessageData("Juhuu",
            "",
            Timestamp(Date()),
            com.google.android.gms.maps.model.LatLng(27.0,64.0).toString(),
            1,
            "i69kfXgRYlR3EzhE4KHe9plDeVd2")

        val user = FirebaseAuth.getInstance().currentUser
        user?.let{
            name = user.displayName
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