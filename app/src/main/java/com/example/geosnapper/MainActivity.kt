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

        // Testinapilla avataan karttanäkymä
        binding.buttonTest1.setOnClickListener {
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