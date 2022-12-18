package com.example.geosnapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.geosnapper.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.math.BigInteger
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnReset.setOnClickListener{
            binding.etEmail.setText("")
            binding.etPassword.setText("")
            binding.etConfirmPassword.setText("")
        }

        binding.textView.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }

        binding.btnSubmit.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = hasher(binding.etPassword.text.toString())
            val confirmPassword = hasher(binding.etConfirmPassword.text.toString())

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, R.string.passwords_not_same, Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun hasher(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val base16Hash = BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')
        return Base64.encodeToString(base16Hash.toByteArray(), 16).trim()
    }
}