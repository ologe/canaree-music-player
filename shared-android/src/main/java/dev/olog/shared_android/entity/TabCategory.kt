package dev.olog.shared_android.entity

import android.content.Context
import dev.olog.shared_android.R

enum class TabCategory {

    FOLDERS, PLAYLISTS, SONGS, ALBUMS, ARTISTS, GENRES,
    RECENT_ALBUMS, RECENT_ARTISTS;

    companion object {
        fun mapStringToCategory(context: Context, category: String): TabCategory {
            return when (category){
                context.getString(R.string.category_folders) -> TabCategory.FOLDERS
                context.getString(R.string.category_playlists) -> TabCategory.PLAYLISTS
                context.getString(R.string.category_songs) -> TabCategory.SONGS
                context.getString(R.string.category_albums) -> TabCategory.ALBUMS
                context.getString(R.string.category_artists) -> TabCategory.ARTISTS
                context.getString(R.string.category_genres) -> TabCategory.GENRES
                else -> throw IllegalArgumentException("invalid category $category")
            }
        }
    }

}

