package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.geosnapper.databinding.ActivityLoginBinding
import com.example.geosnapper.databinding.ActivityMediaBinding
import com.google.firebase.Timestamp
import java.util.*
import kotlin.math.log

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val db = Database()
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener{
            binding.editText.setText("")
        }

        binding.btnSubmit.setOnClickListener{
            val message = binding.editText.text.toString()
            val messageObject = MessageData(message, "", Timestamp(Date()), com.google.android.gms.maps.model.LatLng(27.0,64.0), 1, "i69kfXgRYlR3EzhE4KHe9plDeVd2");
            db.addMessage(messageObject)
            Log.d("Media Activity", message)
        }
    }
}