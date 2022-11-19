package com.example.geosnapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.geosnapper.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

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
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d("Signup Activity", "Oli muuten succesful")
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                            Log.d("Signup Error", it.exception.toString())  // Toast ei jostain syystä tulosta koko virheilmoa niin lisäsin tulosteen lokiin
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Passwords are not the same!", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Empty fields are not allowed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}