package com.example.geosnapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.geosnapper.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnReset.setOnClickListener{
            binding.etEmail.setText("")
            binding.etPassword.setText("")
        }

        binding.textView.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent);
        }

        binding.btnSubmit.setOnClickListener{
//            val email = binding.etEmail.text.toString()
//            val password = binding.etPassword.text.toString()
            // iffin sisään tulee julkaisuversiossa if(email.isNotEmpty() && password.isNotEmpty())
            val temporaryCondition = true

            if (temporaryCondition){
                    firebaseAuth.signInWithEmailAndPassword("teronsahkoposti@gmail.com", "tero1234").addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d("Login Activity", "Login oli muuten succesful")
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("login", "true")
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else{
                Toast.makeText(this, "Empty fields are not allowed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}