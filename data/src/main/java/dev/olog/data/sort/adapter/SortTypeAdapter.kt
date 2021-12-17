package dev.olog.data.sort.adapter

import com.squareup.sqldelight.ColumnAdapter
import dev.olog.data.sort.SortTypeEntity

internal object SortTypeAdapter : ColumnAdapter<SortTypeEntity, String> {

    override fun decode(databaseValue: String): SortTypeEntity {
        return SortTypeEntity.values().first { it.serialized == databaseValue }
    }

    override fun encode(value: SortTypeEntity): String = value.serialized

}