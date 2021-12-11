@file:Suppress("TestFunctionName")

package dev.olog.data

import dev.olog.data.author.Artists_view
import dev.olog.data.author.Podcast_authors_view
import dev.olog.data.index.Indexed_playables

fun IndexedPlayables(
    id: Long,
    is_podcast: Boolean,
    author_id: Long = 0,
    collection_id: Long = 0,
    title: String = "",
    author: String = "",
    album_artist: String = "",
    collection: String = "",
    duration: Long = 0,
    date_added: Long = 0,
    directory: String = "",
    path: String = "",
    disc_number: Int = 0,
    track_number: Int = 0,
) = Indexed_playables(
    id = id,
    author_id = author_id,
    collection_id = collection_id,
    title = title,
    author = author,
    album_artist = album_artist,
    collection = collection,
    duration = duration,
    date_added = date_added,
    directory = directory,
    path = path,
    disc_number = disc_number,
    track_number = track_number,
    is_podcast = is_podcast,
)

fun ArtistView(
    id: Long,
    songs: Long,
    name: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Artists_view(
    id = id,
    name = name,
    songs = songs,
    dateAdded = dateAdded,
    directory = directory
)

fun PodcastAuthorView(
    id: Long,
    episodes: Long,
    name: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Podcast_authors_view(
    id = id,
    name = name,
    episodes = episodes,
    dateAdded = dateAdded,
    directory = directory
)