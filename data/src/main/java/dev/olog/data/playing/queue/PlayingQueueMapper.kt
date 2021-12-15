package dev.olog.data.playing.queue

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.data.playingQueue.SelectAll

internal fun SelectAll.toDomain() = PlayingQueueSong(
    song = Song(
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
    ),
    playOrder = play_order,
)