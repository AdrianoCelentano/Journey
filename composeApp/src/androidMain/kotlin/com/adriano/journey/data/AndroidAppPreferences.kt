package com.adriano.journey.data

import android.content.Context
import android.content.SharedPreferences

class AndroidAppPreferences(context: Context) : AppPreferences {
    private val prefs: SharedPreferences = context.getSharedPreferences("journey_prefs", Context.MODE_PRIVATE)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
