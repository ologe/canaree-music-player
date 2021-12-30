package dev.olog.data.author

import dev.olog.core.author.Artist
import dev.olog.core.MediaUri

// TODO for some reason is not autogenerated
data class Artists_view(
    val id: String,
    val name: String,
    val songs: Long,
    val dateAdded: Long,
    val directory: String,
)

// TODO for some reason is not autogenerated
data class Podcast_authors_view(
    val id: String,
    val name: String,
    val episodes: Long,
    val dateAdded: Long,
    val directory: String,
)

internal fun Artists_view.toDomain() = Artist(
    uri = MediaUri.invoke(
        source = MediaUri.Source.MediaStore,
        category = MediaUri.Category.Author,
        id = id,
        isPodcast = false,
    ),
    name = name,
    songs = songs.toInt(),
)

internal fun Podcast_authors_view.toDomain() = Artist(
    uri = MediaUri.invoke(
        source = MediaUri.Source.MediaStore,
        category = MediaUri.Category.Author,
        id = id,
        isPodcast = true
    ),
    name = name,
    songs = episodes.toInt(),
)