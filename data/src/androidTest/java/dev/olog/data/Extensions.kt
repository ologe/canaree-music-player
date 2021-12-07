package dev.olog.data

import dev.olog.data.index.IndexedPlayablesQueries
import dev.olog.data.index.Indexed_playables

fun IndexedPlayablesQueries.insertGroup(data: List<Indexed_playables>) {
    for (item in data) {
        insert(item)
    }
}