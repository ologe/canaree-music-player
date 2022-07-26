package dev.olog.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.olog.data.db.equalizer.EqualizerBandEntity

class CustomTypeConverters {

    private val gson by lazy { Gson() }

    @TypeConverter
    fun fromString(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.toJson(list, listType)
    }
}