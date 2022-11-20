package dev.olog.data.db.entities

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

@ProvidedTypeConverter
internal class CustomTypeConverters @Inject constructor(
    private val gson: Gson
) {

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