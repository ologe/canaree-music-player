package dev.olog.lib.db

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import dev.olog.lib.model.db.EqualizerBandEntity
import dev.olog.lib.network.SharedNetworkModule

internal object CustomTypeConverters {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<EqualizerBandEntity> {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return SharedNetworkModule.gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromArrayList(list: List<EqualizerBandEntity>): String {
        val listType = object : TypeToken<List<EqualizerBandEntity>>() {}.type
        return SharedNetworkModule.gson.toJson(list, listType)
    }
}