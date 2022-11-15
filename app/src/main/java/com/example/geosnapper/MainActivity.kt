package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.geosnapper.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //When initializing your activity, check to see if the user is currently signed in
        /**
         * public void onStart() {
         *  super.onStart()
         *  FirebaseUser currentUser = mAuth.getCurrentUser()
         *  updateUI(currentUser)
         *
         *  //Access user information
         *  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser()
         *  if(user != null) {
         *      String name = user.getDisplayName()
         *      String email = user.getEmail()
         *      Uri
         */

        // Testinapilla avataan karttanäkymä
        binding.buttonTest1.setOnClickListener {
        val passedValue=intent.getStringExtra("login")

        if (passedValue == null) {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent);
        }
        else if (passedValue == "true"){
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent);
        }

        // Testinapilla avataan profiilinäkymä
        binding.buttonTest2.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent);
        }

        binding.buttonTest3.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent);
        }
    }
}