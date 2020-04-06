package dev.olog.presentation.model

import android.content.Context
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.MediaIdCategory.*
import dev.olog.presentation.R
import dev.olog.shared.throwNotHandled

data class LibraryCategoryBehavior(
    val category: MediaIdCategory,
    var visible: Boolean,
    var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            FOLDERS -> R.string.category_folders
            PLAYLISTS,
            PODCASTS_PLAYLIST -> R.string.category_playlists
            SONGS -> R.string.category_songs
            ALBUMS -> R.string.category_albums
            ARTISTS -> R.string.category_artists
            PODCASTS_AUTHORS -> R.string.category_podcast_authors
            GENRES -> R.string.category_genres
            PODCASTS -> R.string.category_podcasts
            SPOTIFY_ALBUMS -> throwNotHandled(category)
            SPOTIFY_TRACK -> throwNotHandled(category)
        }
        return context.getString(stringId)
    }

}

