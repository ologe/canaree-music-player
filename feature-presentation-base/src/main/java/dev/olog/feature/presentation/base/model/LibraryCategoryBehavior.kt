package dev.olog.feature.presentation.base.model

import android.content.Context
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.MediaIdCategory.*
import dev.olog.feature.presentation.base.R
import dev.olog.shared.throwNotHandled

data class LibraryCategoryBehavior(
    val category: MediaIdCategory,
    var visible: Boolean,
    var order: Int
) {

    fun asString(context: Context): String {
        val stringId = when (category) {
            FOLDERS -> R.string.common_folders
            PLAYLISTS,
            PODCASTS_PLAYLIST -> R.string.common_playlists
            SONGS -> R.string.common_tracks
            ALBUMS -> R.string.common_albums
            ARTISTS -> R.string.common_artists
            PODCASTS_AUTHORS -> R.string.common_podcast_authors
            GENRES -> R.string.common_genres
            PODCASTS -> R.string.common_podcasts
            SPOTIFY_ALBUMS -> throwNotHandled(category)
            SPOTIFY_TRACK -> throwNotHandled(category)
        }
        return context.getString(stringId)
    }

}

