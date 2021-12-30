package dev.olog.data.db

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
    media_uriAdapter = MediaUriAdapter
)