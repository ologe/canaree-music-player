package dev.olog.testing

import dev.olog.data.playingQueue.SelectAll

fun emptySelectAll() = SelectAll(
    id = 0,
    author_id = 0,
    collection_id = 0,
    title = "",
    author = "",
    album_artist = "",
    collection = "",
    duration = 0,
    date_added = 0,
    directory = "",
    path = "",
    disc_number = 0,
    track_number = 0,
    is_podcast = false,
    play_order = 0
)