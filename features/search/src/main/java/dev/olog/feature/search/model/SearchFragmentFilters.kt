package dev.olog.feature.search.model

import dev.olog.domain.entity.track.*

@JvmName("filterTracks")
internal fun List<Track>.filterBy(query: String): List<Track> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.title.contains(query, true) ||
            it.artist.contains(query, true) ||
            it.album.contains(query, true)
    }
}

@JvmName("filterAlbums")
internal fun List<Album>.filterBy(query: String): List<Album> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.title.contains(query, true) || it.artist.contains(query, true)
    }
}

@JvmName("filterArtists")
internal fun List<Artist>.filterBy(query: String): List<Artist> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.name.contains(query, true)
    }
}

@JvmName("filterPlaylists")
internal fun List<Playlist>.filterBy(query: String): List<Playlist> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.title.contains(query, true)
    }
}

@JvmName("filterGenres")
internal fun List<Genre>.filterBy(query: String): List<Genre> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.name.contains(query, true)
    }
}

@JvmName("filterFolders")
internal fun List<Folder>.filterBy(query: String): List<Folder> {
    if (query.isBlank()) {
        return emptyList()
    }
    return filter {
        it.title.contains(query, true)
    }
}