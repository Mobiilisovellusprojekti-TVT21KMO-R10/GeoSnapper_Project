package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.geosnapper.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_about)
    }
}