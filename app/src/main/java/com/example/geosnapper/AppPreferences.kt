package com.example.geosnapper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


class Preferences (context: Context) {
    private val LOGIN_DATA = "loginData"
    private val SETINGS_DATA = "setingsData"
    var preferences = context.getSharedPreferences("SETINGS_DATA", Activity.MODE_PRIVATE)
    var preferences = context.getSharedPreferences("SETINGS_DATA", Activity.MODE_PRIVATE)


    fun saveLoginData(userName: String, password: String) {

    }

    



}