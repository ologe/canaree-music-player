package dev.olog.feature.library.library

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.feature.library.R

data class LibraryFragmentCategoryState(
    val category: MediaIdCategory,
    var visible: Boolean,
    var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            MediaIdCategory.FOLDERS -> R.string.category_folders
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> R.string.category_playlists
            MediaIdCategory.SONGS -> R.string.category_songs
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> R.string.category_albums
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> R.string.category_artists
            MediaIdCategory.GENRES -> R.string.category_genres
            MediaIdCategory.PODCASTS -> R.string.category_podcasts
            else -> 0 //will throw an exception
        }
        return context.getString(stringId)
    }

}

