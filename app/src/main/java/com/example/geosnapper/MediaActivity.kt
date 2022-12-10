package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.geosnapper.dataHandling.Database
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.databinding.ActivityMediaBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import java.util.*

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val db = Database()
        super.onCreate(savedInstanceState)

        val passedValue = intent.getStringExtra("lat")
        val coordinates =  LatLng(
            intent.getStringExtra("lat")!!.toDouble(),
            intent.getStringExtra("lng")!!.toDouble()
        )

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener{
            binding.editText.setText("")
        }

        binding.btnSubmit.setOnClickListener{
            val message = binding.editText.text.toString()

            Log.d("Media Activity", "nappi toimii")

            if (passedValue != null){
                val messageObject = MessageData(message, "", Timestamp(Date()), coordinates, 3, LocalStorage.getUserId());
                db.addMessage(messageObject)
                Log.d("Media Activity", message)
                finish()
            }
            else
            {
                Log.d("Media Activity", "mentiin elseen")
            }
        }
    }
}