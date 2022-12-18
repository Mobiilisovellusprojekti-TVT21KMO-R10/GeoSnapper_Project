
package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.res.Configuration
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalStorage.setup(applicationContext)
        LocalStorage.setViewDistance(0)

        val locale = Locale(LocalStorage.getLanguage())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )


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