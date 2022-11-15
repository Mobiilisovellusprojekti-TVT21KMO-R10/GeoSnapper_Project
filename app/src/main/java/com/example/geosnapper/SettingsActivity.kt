package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.geosnapper.databinding.ActivityProfileBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}