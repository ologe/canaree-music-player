package dev.olog.domain

// TODO rename this, causes confusiont with [MediaId.Category], maybe to MediaIdType
enum class MediaIdCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_AUTHORS,

    SPOTIFY_ALBUMS,
    SPOTIFY_TRACK
}