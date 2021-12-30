package dev.olog.data.playing.queue

import dev.olog.core.MediaUri
import dev.olog.core.queue.PlayingQueueSong
import dev.olog.core.track.Song
import dev.olog.data.playingQueue.SelectAll

internal fun SelectAll.toDomain() = PlayingQueueSong(
    song = Song(
        uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = id,
            isPodcast = is_podcast,
        ),
        artistUri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Author,
            id = author_id,
            isPodcast = is_podcast,
        ),
        albumUri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Collection,
            id = collection_id,
            isPodcast = is_podcast,
        ),
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
    ),
    playOrder = play_order,
)