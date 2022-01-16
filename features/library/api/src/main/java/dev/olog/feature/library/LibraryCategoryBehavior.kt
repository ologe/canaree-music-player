package dev.olog.feature.library

import android.content.Context
import dev.olog.core.MediaUri

data class LibraryCategoryBehavior(
    val category: MediaUri.Category,
    var visible: Boolean,
    var order: Int,
    private val isPodcast: Boolean,
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            MediaUri.Category.Folder -> localization.R.string.category_folders
            MediaUri.Category.Playlist -> localization.R.string.category_playlists
            MediaUri.Category.Collection -> localization.R.string.category_albums
            MediaUri.Category.Author -> localization.R.string.category_artists
            MediaUri.Category.Genre -> localization.R.string.category_genres
            MediaUri.Category.Track -> {
                if (isPodcast) {
                    localization.R.string.category_podcasts
                } else {
                    localization.R.string.category_songs
                }
            }
        }
        return context.getString(stringId)
    }

}

