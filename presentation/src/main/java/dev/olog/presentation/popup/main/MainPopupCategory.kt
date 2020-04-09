package dev.olog.presentation.popup.main

import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.shared.throwNotHandled

internal enum class MainPopupCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_AUTHORS,

    PLAYING_QUEUE,
    SEARCH;

//    fun toTabCategory(): TabCategory { TODO
//        return when (this) {
//            PLAYING_QUEUE,
//            SEARCH -> throwNotHandled(this)
//            FOLDERS -> TabCategory.FOLDERS
//            PLAYLISTS -> TabCategory.PLAYLISTS
//            SONGS -> TabCategory.SONGS
//            ALBUMS -> TabCategory.ALBUMS
//            ARTISTS -> TabCategory.ARTISTS
//            GENRES -> TabCategory.GENRES
//            PODCASTS_PLAYLIST -> TabCategory.PODCASTS_PLAYLIST
//            PODCASTS -> TabCategory.PODCASTS
//            PODCASTS_AUTHORS -> TabCategory.PODCASTS_AUTHORS
//        }
//    }

}

internal fun PresentationIdCategory.toMainPopupCategory(): MainPopupCategory {
    return when (this) {
        PresentationIdCategory.HEADER -> throwNotHandled(this)
        PresentationIdCategory.FOLDERS -> MainPopupCategory.FOLDERS
        PresentationIdCategory.PLAYLISTS -> MainPopupCategory.PLAYLISTS
        PresentationIdCategory.SONGS -> MainPopupCategory.SONGS
        PresentationIdCategory.ALBUMS -> MainPopupCategory.ALBUMS
        PresentationIdCategory.ARTISTS -> MainPopupCategory.ARTISTS
        PresentationIdCategory.GENRES -> MainPopupCategory.GENRES
        PresentationIdCategory.PODCASTS_PLAYLIST -> MainPopupCategory.PODCASTS_PLAYLIST
        PresentationIdCategory.PODCASTS -> MainPopupCategory.PODCASTS
        PresentationIdCategory.PODCASTS_AUTHORS -> MainPopupCategory.PODCASTS_AUTHORS
        PresentationIdCategory.SPOTIFY_ALBUMS -> throwNotHandled(this)
        PresentationIdCategory.SPOTIFY_TRACK -> throwNotHandled(this)
    }
}