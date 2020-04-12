package dev.olog.feature.service.music.helper

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.annotation.StringRes
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.service.music.R

internal object MediaIdHelper {

    const val MEDIA_ID_ROOT = "__ROOT__"

    private val PLAYLISTS = MediaIdCategory.PLAYLISTS.toString()
    private val SONGS = MediaIdCategory.SONGS.toString()
    private val ALBUMS = MediaIdCategory.ALBUMS.toString()
    private val ARTISTS = MediaIdCategory.ARTISTS.toString()
    private val GENRES = MediaIdCategory.GENRES.toString()

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
            .setMediaId(MediaId.SHUFFLE_ID.toString())
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