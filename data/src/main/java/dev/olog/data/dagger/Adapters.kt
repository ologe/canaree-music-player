package dev.olog.data.dagger

import com.squareup.sqldelight.ColumnAdapter
import dev.olog.core.MediaId
import dev.olog.data.Recent_searches
import dev.olog.data.sort.Sort
import dev.olog.data.sort.adapter.SortDirectionAdapter
import dev.olog.data.sort.adapter.SortTableAdapter
import dev.olog.data.sort.adapter.SortTypeAdapter

internal val SortAdapter = Sort.Adapter(
    table_nameAdapter = SortTableAdapter,
    column_nameAdapter = SortTypeAdapter,
    directionAdapter = SortDirectionAdapter,
)

internal val RecentSearchesAdapter = Recent_searches.Adapter(
    media_idAdapter = MediaIdAdapter
)

internal object MediaIdAdapter : ColumnAdapter<MediaId, String> {

    override fun decode(databaseValue: String): MediaId {
        return MediaId.fromString(databaseValue)
    }

    override fun encode(value: MediaId): String {
        return value.toString()
    }
}