package com.example.geosnapper.dataHandling

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN

object LocalStorage {
    // TALLENNETTAVA TIEDOSTO
    private const val USER_DATA = "USER_DATA"
    // TALLENNETTAVAT TIEDOT
    private const val USER_EMAIL = "USER_EMAIL"
    private const val USER_PASSWORD = "PASSWORD"
    private const val USER_ID = "USER_ID"
    private const val VIEW_DISTANCE = "VIEW_DISTANCE"

    private var userPrefs : SharedPreferences? = null

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    private var SharedPreferences.email
        get() = getString(USER_EMAIL, "Pekka")
        set(value) {
            editMe {
                it.putString(USER_EMAIL, value)
            }
        }

    private var SharedPreferences.password
        get() = getString(USER_PASSWORD, "Sauri")
        set(value) {
            editMe {
                it.putString(USER_PASSWORD, value)
            }
        }

    private var SharedPreferences.uid
        get() = getString(USER_ID, "")
        set(value) {
            editMe {
                it.putString(USER_ID, value)
            }
        }

    private var SharedPreferences.viewDistance
        get() = getInt(VIEW_DISTANCE, 0)
        set(value) {
            editMe {
                it.putInt(VIEW_DISTANCE, value)
            }
        }

    private var SharedPreferences.initialize
        get() = true
        set(value) {
            editMe {
                it.remove(USER_EMAIL)
                it.remove(USER_PASSWORD)
                it.remove(USER_ID)
                it.remove(VIEW_DISTANCE)
            }
        }

    fun setup(context: Context) {
        userPrefs  = context.getSharedPreferences(USER_DATA, MODE_PRIVATE)
    }

    fun saveLoginData(email: String, password: String, userId: String) {
        userPrefs?.email = email
        userPrefs?.password = password
        userPrefs?.uid = userId
    }

    fun setViewDistance(distance: Int) {
        userPrefs?.viewDistance = distance
    }

    fun initialize() {
        userPrefs?.initialize = true
    }

    fun getEmail(): String {
        return userPrefs?.email.toString()
    }

    fun getPassword(): String {
        return userPrefs?.password.toString()
    }

    fun getUserId(): String {
        return userPrefs?.uid.toString()
    }

    fun getViewDistance(): Int {
        return userPrefs!!.viewDistance
    }
}
