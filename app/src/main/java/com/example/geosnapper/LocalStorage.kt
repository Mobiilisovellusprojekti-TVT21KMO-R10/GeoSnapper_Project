package com.example.geosnapper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

open class LocalStorage {
    // TALLENNETTAVA TIEDOSTO
    // TALLENNETTAVA TIEDOSTO
    private val USER_DATA = "USER_DATA"
    // TALLENNETTAVAT TIEDOT
    private val USER_EMAIL = "USER_EMAIL"
    private val USER_PASSWORD = "PASSWORD"
    private val VIEW_DISTANCE = "VIEW_DISTANCE"

    private var userPrefs : SharedPreferences? = null
    fun setup(context: Context) {
        userPrefs  = context.getSharedPreferences(USER_DATA, MODE_PRIVATE)
    }

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    private var SharedPreferences.email
        get() = getString(USER_EMAIL, "")
        set(value) {
            editMe {
                it.putString(USER_EMAIL, value)
            }
        }

    private var SharedPreferences.password
        get() = getString(USER_PASSWORD, "")
        set(value) {
            editMe {
                it.putString(USER_PASSWORD, value)
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
        get() = null
        set(value) {
            editMe {
                it.remove(USER_EMAIL)
                it.remove(USER_PASSWORD)
                it.remove(VIEW_DISTANCE)
            }
        }

    fun saveLoginData(email: String, password: String) {
        userPrefs?.email = email
        userPrefs?.password = password
    }

    fun setViewDistance(distance: Int) {
        userPrefs?.viewDistance = distance
    }

    fun initialize() {
        userPrefs?.initialize
    }

    fun getEmail(): String {
        return userPrefs?.email.toString()
    }

    fun getPassword(): String {
        return userPrefs?.password.toString()
    }

    fun getViewDistance(): Int? {
        return userPrefs?.viewDistance
    }
}
