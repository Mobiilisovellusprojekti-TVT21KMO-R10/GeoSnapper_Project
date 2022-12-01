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
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPreferences()

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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            // iffin sisään tulee julkaisuversiossa if(email.isNotEmpty() && password.isNotEmpty())
            val temporaryCondition = true

            if (temporaryCondition){
                    firebaseAuth.signInWithEmailAndPassword("teronsahkoposti@gmail.com", "tero1234").addOnCompleteListener {
                        if (it.isSuccessful){
                            val user = firebaseAuth.currentUser
                            user?.let {
                                uid = user.uid
                            }
                            Log.d("Login Activity", "Login oli muuten succesful")


                            // MÄÄ RÄPELSIN TÄTÄ SEN VERRAN, ETTÄ TALLENNETAAN TÄSSÄ KÄYTTÄJÄTIEDOT
                            // APP AUKAISTAAN FUNKTIOSSA
                            // TARTTIS VIELÄ MIETTIÄ, ETTÄ MITEN VARMISTETAAN TALLENNETUT KÄYTTÄJÄTIEDOT
                            LocalStorage().saveLoginData(email, password)
                            openApp(uid)

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

    private fun openApp(uid: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", uid)
        startActivity(intent)
    }

    // TÄÄ ON HAHMOTTELUVAIHEESSA PITÄIS KEKSIÄ TOIMIVA NULLCHECK
    private fun checkPreferences() {
        /*
        val email = checkNotNull(LocalStorage().getEmail())
        val password = checkNotNull(LocalStorage().getPassword())


            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        uid = user.uid
                    }
                    openApp(uid)
                }
            }
        */
    }

}