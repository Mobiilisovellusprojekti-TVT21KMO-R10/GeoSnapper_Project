package com.example.geosnapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.math.BigInteger
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ifSavedLogInData()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val password = hasher(binding.etPassword.text.toString())

            LocalStorage.saveLoginData("asd", "asd", "asdasdadd")
            openApp("asddd")
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseLogIn(email,password)
            }
            else {
                Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseLogIn(email: String, password: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        uid = user.uid
                    }
                    LocalStorage.saveLoginData(email, password, uid)
                    openApp(uid)
                }
                else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openApp(uid: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", uid)
        startActivity(intent)
    }

    private fun ifSavedLogInData() {
        val email = LocalStorage.getEmail()
        val password = LocalStorage.getPassword()
        if (email != "Pekka" && password != "Sauri") {
            firebaseLogIn(email, password)
        }
    }

    fun hasher(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val base16Hash = BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')
        return Base64.encodeToString(base16Hash.toByteArray(), 16).trim()
    }
}