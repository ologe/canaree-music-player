package dev.olog.data.sort.adapter

import com.squareup.sqldelight.ColumnAdapter
import dev.olog.data.sort.SortTable

internal object SortTableAdapter : ColumnAdapter<SortTable, String> {

    override fun decode(databaseValue: String): SortTable {
        return SortTable.values().first { it.serialized == databaseValue }
    }

    override fun encode(value: SortTable): String = value.serialized

}