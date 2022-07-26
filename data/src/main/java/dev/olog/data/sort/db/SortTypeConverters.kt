package dev.olog.data.sort.db

import androidx.room.TypeConverter

class SortTypeConverters {

    @TypeConverter
    fun deserializeSortTable(value: String): SortEntityTable {
        return SortEntityTable.values().first { it.tableName == value }
    }

    @TypeConverter
    fun serializeSortTable(value: SortEntityTable): String {
        return value.tableName
    }

    @TypeConverter
    fun deserializeSortDirection(value: String): SortDirectionEntity {
        return SortDirectionEntity.values().first { it.serializedValue == value }
    }

    @TypeConverter
    fun serializeSortDirection(value: SortDirectionEntity): String {
        return value.serializedValue
    }

    @TypeConverter
    fun deserializeSortType(value: String): SortTypeEntity {
        return SortTypeEntity.values().first { it.columnName == value }
    }

    @TypeConverter
    fun serializeSortType(value: SortTypeEntity): String {
        return value.columnName
    }

}