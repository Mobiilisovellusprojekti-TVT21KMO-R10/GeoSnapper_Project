package com.example.geosnapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.geosnapper.databinding.ActivityLoginBinding
import com.example.geosnapper.databinding.ActivityMediaBinding
import kotlin.math.log

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener{
            binding.editText.setText("")
        }

        binding.btnSubmit.setOnClickListener{
            val message = binding.editText.text.toString()
            Log.d("Media Activity", message)
        }
    }
}