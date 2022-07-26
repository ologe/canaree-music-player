package dev.olog.data.sort.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sort")
data class SortEntity(
    @PrimaryKey
    val tableName: SortEntityTable,
    val columnName: SortTypeEntity,
    val direction: SortDirectionEntity
)