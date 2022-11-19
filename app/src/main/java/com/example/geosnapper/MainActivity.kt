package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.geosnapper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val passedValue=intent.getStringExtra("login")

        if (passedValue == null) {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent);
        }
        else if (passedValue == "true"){
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent);
        }
    }
}