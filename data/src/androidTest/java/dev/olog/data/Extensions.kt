package dev.olog.data

import dev.olog.data.index.*

fun IndexedPlayablesQueries.insertGroup(data: List<Indexed_playables>) {
    for (item in data) {
        insert(item)
    }
}

fun IndexedGenresQueries.insertGroup(data: List<Indexed_genres>) {
    for (item in data) {
        insert(item)
    }
}

fun IndexedGenresQueries.insertPlayableGroup(data: List<Indexed_genres_playables>) {
    for (item in data) {
        insertPlayable(item)
    }
}

fun IndexedPlaylistsQueries.insertGroup(data: List<Indexed_playlists>) {
    for (item in data) {
        insert(item)
    }
}

fun IndexedPlaylistsQueries.insertPlayableGroup(data: List<Indexed_playlists_playables>) {
    for (item in data) {
        insertPlayable(item)
    }
}