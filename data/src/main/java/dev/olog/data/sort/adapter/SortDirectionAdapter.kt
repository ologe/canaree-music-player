package dev.olog.data.sort.adapter

import com.squareup.sqldelight.ColumnAdapter
import dev.olog.core.entity.sort.SortDirection

internal object SortDirectionAdapter : ColumnAdapter<SortDirection, String> {

    override fun decode(databaseValue: String): SortDirection {
        return SortDirection.values().first { it.serialized == databaseValue }
    }

    override fun encode(value: SortDirection): String = value.serialized

}