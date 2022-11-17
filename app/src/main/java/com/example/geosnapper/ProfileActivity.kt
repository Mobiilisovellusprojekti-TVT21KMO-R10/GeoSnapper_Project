package com.example.geosnapper

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.geosnapper.databinding.ActivityProfileBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        val db = Firebase.firestore
        val usrMessage = "Jepajee"

        val messageData = hashMapOf(
            "uid" to "i69kfXgRYlR3EzhE4KHe9plDeVd2",
            "message" to usrMessage,
            "geoData" to com.google.android.gms.maps.model.LatLng(27.0, 64.0),
            "mediaLink" to "",
            "tier"  to 1,
            "created" to Timestamp(Date())
        )

        db.collection("messageData")
            .add(messageData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapchot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                Log.w(TAG, "Error adding document", e)
            }

        val user = FirebaseAuth.getInstance().currentUser
        user?.let{
            name = user.displayName
            email = user.email
            uid = user.uid
        }

        var myArray: List<Any> = listOf()
        val collection = db.collection("messageData")
        val userObject = collection.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.data}")
                }
                myArray.map {
                     }
                Log.d("myArray", myArray.toString())
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting userData", exception)
            }




                /*
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting userData", exception)
            }*/


        binding.collapsingToolbarProfileView.title = email


        Log.d("Profile Activity","uid = ${uid}" )

        binding.buttonToSettings.setOnClickListener() {
            startActivity(Intent(this, SettingsActivity::class.java))     }
    }
}