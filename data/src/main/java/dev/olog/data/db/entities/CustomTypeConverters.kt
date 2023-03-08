package dev.olog.data.db.entities

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class CustomTypeConverters @Inject constructor(
    private val serializer: Json,
) {

    @TypeConverter
    fun fromString(value: String): List<EqualizerBandEntity> {
        return serializer.decodeFromString(value)
    }

    @TypeConverter
    fun fromArrayList(list: List<EqualizerBandEntity>): String {
        return serializer.encodeToString(list)
    }
}