package com.example.geosnapper

import android.content.Intent
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
        val mapActivity = MapActivity()
        super.onCreate(savedInstanceState)

        val passedValue = intent.getStringExtra("userId")

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener{
            binding.editText.setText("")
        }

        binding.btnSubmit.setOnClickListener{
            val message = binding.editText.text.toString()

            Log.d("Media Activity", "nappi toimii")

            if (passedValue != null){
                val messageObject = MessageData(message, "", Timestamp(Date()), mapActivity.getLocation(), 1, passedValue);
                db.addMessage(messageObject)
                Log.d("Media Activity", message)

                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent);
            }
            else
            {
                Log.d("Media Activity", "mentiin elseen")
            }
        }
    }
}