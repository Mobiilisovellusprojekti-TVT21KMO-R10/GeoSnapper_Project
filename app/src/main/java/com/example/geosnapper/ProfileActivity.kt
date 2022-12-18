package com.example.geosnapper

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.geosnapper.dataHandling.Database
import com.example.geosnapper.dataHandling.LocalStorage
import com.example.geosnapper.databinding.ActivityProfileBinding
import com.google.android.material.slider.Slider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*
import kotlin.system.exitProcess

class ProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var languages = arrayOf("","ENG", "FI")
    val NEW_SPINNER_ID = 1

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var name: String? = ""
        var email: String? = ""
        var uid: String? = ""
        var myMessages: List<Any>? = listOf()

        firebaseAuth = FirebaseAuth.getInstance()
        fun fetchMessages(uid: String?){
            val db = Firebase.firestore

            val collection = db.collection("messageData")
            collection.whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { documents ->
                    binding.linearLayoutProfileInner.removeAllViews()
                    for (document in documents) {
                        val gson = Gson()
                        val messageData = gson.toJson(document.data)

                        val messageView = TextView(this)
                        messageView.text = messageData


                        binding.linearLayoutProfileInner.addView(messageView)

                    }
                }
                .addOnFailureListener { exception ->

                }
        }

        val messageObject = MessageData("Juhuu",
            "",
            Timestamp(Date()),
            com.google.android.gms.maps.model.LatLng(27.0,64.0),
            1,
            "i69kfXgRYlR3EzhE4KHe9plDeVd2")

        val user = FirebaseAuth.getInstance().currentUser
        user?.let{
            name = user.displayName
            email = user.email
            uid = user.uid
        }

        val db = Database()

        //db.addMessage(messageObject)
        //fetchMessages(uid)


        setuplanguageSelector()

        binding.collapsingToolbarProfileView.title = email

        binding.buttonShowMessages.setOnClickListener {
            fetchMessages(uid)
        }

        binding.buttonToSettings.setOnClickListener() {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
           finish()
        }

        binding.btnLogOut.setOnClickListener {
            firebaseAuth.signOut()
            LocalStorage.initialize()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val distance = if (LocalStorage.getViewDistance() == 0) 1 else LocalStorage.getViewDistance() / 1000
        binding.slider.value = distance.toFloat()
        binding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }
            override fun onStopTrackingTouch(slider: Slider) {
                val newValue = slider.value * 1000
                LocalStorage.setViewDistance(newValue.toInt())
            }
        })
    }

    private fun setuplanguageSelector() {
        val spinner = Spinner(this)
        spinner.id = NEW_SPINNER_ID

        val ll = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.setMargins(10, 0, 10, 10)
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayoutSpinner)
        linearLayout.addView(spinner)

        val aAdapter = ArrayAdapter(this, R.layout.spinner_right_aligned, languages)
        aAdapter.setDropDownViewResource(R.layout.spinner_right_aligned)

        with(spinner)
        {
            adapter = aAdapter
            setSelection(0, true)
            onItemSelectedListener = this@ProfileActivity
            layoutParams = ll
            prompt = "Select language"
        }
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val locale = when (position) {
            1 -> "en"
            2 -> "fi"
            else -> "null"
        }
        if (locale != LocalStorage.getLanguage() && locale != "null") {
            LocalStorage.setLanguage(locale)
            val config = resources.configuration
            val locale = Locale(locale)
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            finish()
            overridePendingTransition( 0, 0)
            startActivity(Intent(this, MapActivity::class.java))
            startActivity(getIntent())
            overridePendingTransition( 0, 0)
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        exitProcess(0)
    }
}