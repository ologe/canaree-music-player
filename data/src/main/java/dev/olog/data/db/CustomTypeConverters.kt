package dev.olog.data.db

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import dev.olog.data.model.db.EqualizerBandEntity
import dev.olog.lib.network.SharedNetworkModule

internal object CustomTypeConverters {

    @TypeConverter
    @JvmStatic
    fun serializeBands(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return SharedNetworkModule.gson.toJson(list, listType)
    }

    @TypeConverter
    @JvmStatic
    fun deserializeBands(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return SharedNetworkModule.gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun serializeLongs(list: List<Long>): String {
        val listType = object : TypeToken<List<Long>>() {}.type
        return SharedNetworkModule.gson.toJson(list, listType)
    }

    @TypeConverter
    @JvmStatic
    fun deserializeLongs(value: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return SharedNetworkModule.gson.fromJson(value, listType)
    }

}