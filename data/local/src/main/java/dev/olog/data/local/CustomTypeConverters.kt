package dev.olog.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.olog.domain.RecentSearchesType
import dev.olog.data.local.equalizer.preset.EqualizerBandEntity

internal class CustomTypeConverters {

    private val gson by lazy { Gson() }

    @TypeConverter
    fun deserialize(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun serialize(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return gson.toJson(list, listType)
    }

    @TypeConverter
    fun deserialize(compatibilityValue: Int): RecentSearchesType {
        return RecentSearchesType.values().first { it.compatibilityValue == compatibilityValue }
    }

    @TypeConverter
    fun serialize(item: RecentSearchesType): Int {
        return item.compatibilityValue
    }

}