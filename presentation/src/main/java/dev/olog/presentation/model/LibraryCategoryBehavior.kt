package dev.olog.presentation.model

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R

data class LibraryCategoryBehavior(
    val category: Category,
    var visible: Boolean,
    var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            Category.FOLDERS -> R.string.category_folders
            Category.PLAYLISTS -> R.string.category_playlists
            Category.SONGS -> R.string.category_songs
            Category.ALBUMS -> R.string.category_albums
            Category.ARTISTS -> R.string.category_artists
            Category.GENRES -> R.string.category_genres
            Category.PODCASTS -> R.string.category_podcasts
        }
        return context.getString(stringId)
    }

    enum class Category {
        FOLDERS,
        PLAYLISTS,
        SONGS,
        PODCASTS,
        ALBUMS,
        ARTISTS,
        GENRES;

        fun toMediaId(): MediaIdCategory = when (this) {
            FOLDERS -> MediaIdCategory.FOLDERS
            PLAYLISTS -> MediaIdCategory.PLAYLISTS
            SONGS,
            PODCASTS -> MediaIdCategory.SONGS
            ALBUMS -> MediaIdCategory.ALBUMS
            ARTISTS -> MediaIdCategory.ARTISTS
            GENRES -> MediaIdCategory.GENRES
        }

    }

}

