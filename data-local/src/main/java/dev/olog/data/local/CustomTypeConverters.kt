package dev.olog.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.olog.data.local.equalizer.preset.EqualizerBandEntity

internal object CustomTypeConverters {

    private val gson by lazy { Gson() }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromArrayList(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.toJson(list, listType)
    }
}