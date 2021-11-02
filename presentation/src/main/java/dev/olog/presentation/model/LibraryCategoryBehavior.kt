package dev.olog.presentation.model

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R

data class LibraryCategoryBehavior(
    val category: MediaIdCategory,
    var visible: Boolean,
    var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            MediaIdCategory.FOLDERS -> dev.olog.shared.android.R.string.category_folders
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> dev.olog.shared.android.R.string.category_playlists
            MediaIdCategory.SONGS -> dev.olog.shared.android.R.string.category_songs
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> dev.olog.shared.android.R.string.category_albums
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> dev.olog.shared.android.R.string.category_artists
            MediaIdCategory.GENRES -> dev.olog.shared.android.R.string.category_genres
            MediaIdCategory.PODCASTS -> dev.olog.shared.android.R.string.category_podcasts
            else -> 0 //will throw an exception
        }
        return context.getString(stringId)
    }

}

