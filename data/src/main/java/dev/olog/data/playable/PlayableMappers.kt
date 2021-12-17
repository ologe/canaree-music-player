package dev.olog.data.playable

import dev.olog.core.entity.track.Song
import dev.olog.data.index.Indexed_playables

// use type aliases because sql delight don't generate classes, for views
typealias Songs_view = Indexed_playables
typealias Podcast_episodes_view = Indexed_playables
typealias All_playables_view = Indexed_playables

internal fun Indexed_playables.toDomain() = Song(
    id = id,
    artistId = author_id,
    albumId = collection_id,
    title = title,
    artist = author,
    albumArtist = album_artist,
    album = collection,
    duration = duration,
    dateAdded = date_added,
    directory = directory,
    path = path,
    discNumber = disc_number,
    trackNumber = track_number,
    idInPlaylist = 0, // TODO remove
    isPodcast = is_podcast,
)