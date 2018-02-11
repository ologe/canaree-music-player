package dev.olog.msc.domain.entity

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.utils.MediaIdCategory

data class LibraryCategoryBehavior(
        val category: MediaIdCategory,
        var visible: Boolean,
        var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category){
            MediaIdCategory.FOLDERS -> R.string.category_folders
            MediaIdCategory.PLAYLISTS -> R.string.category_playlists
            MediaIdCategory.SONGS -> R.string.category_songs
            MediaIdCategory.ALBUMS -> R.string.category_albums
            MediaIdCategory.ARTISTS -> R.string.category_artists
            MediaIdCategory.GENRES -> R.string.category_genres
            else -> 0 //will throw an exception
        }
        return context.getString(stringId)
    }

}