package com.example.geosnapper

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.geosnapper.databinding.ActivityProfileBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*
        val db = Firebase.firestore
        val usrMessages = listOf<String>(
            "moi",
            "kukkuu"
        )
        val usrData = hashMapOf(
            "uid" to 1234,
            "userMessages" to usrMessages
        )

        db.collection("userData")
            .add(usrData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapchot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                Log.w(TAG, "Error adding document", e)
            }*/

        binding.buttonToSettings.setOnClickListener() {
            startActivity(Intent(this, SettingsActivity::class.java))     }
    }
}