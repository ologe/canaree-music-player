package dev.olog.feature.media.helper

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.annotation.StringRes
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.media.R

internal object MediaIdHelper {

    const val MEDIA_ID_ROOT = "__ROOT__"

    val PLAYLISTS = MediaIdCategory.PLAYLISTS.toString()
    val SONGS = MediaIdCategory.SONGS.toString()
    val ALBUMS = MediaIdCategory.ALBUMS.toString()
    val ARTISTS = MediaIdCategory.ARTISTS.toString()
    val GENRES = MediaIdCategory.GENRES.toString()

    fun getLibraryCategories(context: Context): MutableList<MediaBrowserCompat.MediaItem> {
        return mutableListOf(
            createShuffleAllMediaIem(context),
            createBrowsableMediaItem(
                context,
                PLAYLISTS,
                R.string.common_playlists
            ),
            createBrowsableMediaItem(
                context,
                SONGS,
                R.string.common_tracks
            ),
            createBrowsableMediaItem(
                context,
                ALBUMS,
                R.string.common_albums
            ),
            createBrowsableMediaItem(
                context,
                ARTISTS,
                R.string.common_artists
            ),
            createBrowsableMediaItem(
                context,
                GENRES,
                R.string.common_genres
            )
        )
    }

    private fun createShuffleAllMediaIem(context: Context): MediaBrowserCompat.MediaItem{
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.shuffleId().toString())
            .setTitle(context.getString(R.string.common_shuffle))
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun createBrowsableMediaItem(context: Context, mediaId: String, @StringRes stringRes: Int): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(mediaId)
                .setTitle(context.getString(stringRes))
                .build()

        return MediaBrowserCompat.MediaItem(
                    description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

}