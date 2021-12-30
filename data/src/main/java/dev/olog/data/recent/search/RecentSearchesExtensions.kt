package dev.olog.data.recent.search

import dev.olog.core.MediaUri
import dev.olog.data.RecentSearchesQueries

internal fun RecentSearchesQueries.insert(
    uri: MediaUri,
    insertion_time: Long,
) {
    insert(
        insertion_time = insertion_time,
        media_uri = uri
    )
}