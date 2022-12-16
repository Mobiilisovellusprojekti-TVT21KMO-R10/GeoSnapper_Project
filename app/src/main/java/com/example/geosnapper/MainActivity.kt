
package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalStorage.setup(applicationContext)

        //tässä muuttujassa on käyttäjän ID.
        val passedValue = intent.getStringExtra("userId")

        if (passedValue == null || passedValue == "null") {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        } else if (passedValue != null) {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("userId", passedValue)
            startActivity(intent);
        }
    }
}