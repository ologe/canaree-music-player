package dev.olog.presentation.model

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R

class LibraryCategoryBehavior(
    @JvmField
    val category: MediaIdCategory,
    @JvmField
    var visible: Boolean,
    @JvmField
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibraryCategoryBehavior

        if (category != other.category) return false
        if (visible != other.visible) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + visible.hashCode()
        result = 31 * result + order
        return result
    }


}

