package com.adriano.journey.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FloatArrayConverter {
    @TypeConverter
    fun fromFloatArray(value: FloatArray?): String? {
        if (value == null) return null
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toFloatArray(value: String?): FloatArray? {
        if (value == null) return null
        val listType = object : TypeToken<FloatArray>() {}.type
        return Gson().fromJson(value, listType)
    }
}
