package dev.olog.msc.music.service.helper

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.msc.R
import dev.olog.msc.utils.MediaIdCategory

object MediaIdHelper {

    const val MEDIA_ID_ROOT = "__ROOT__"

    val FOLDERS = MediaIdCategory.FOLDERS.toString()
    val PLAYLISTS = MediaIdCategory.PLAYLISTS.toString()
    val SONGS = MediaIdCategory.SONGS.toString()
    val ALBUMS = MediaIdCategory.ALBUMS.toString()
    val ARTISTS = MediaIdCategory.ARTISTS.toString()
    val GENRES = MediaIdCategory.GENRES.toString()

    fun getLibraryCategories(context: Context): MutableList<MediaBrowserCompat.MediaItem> {
        return mutableListOf(
                // todo pensare a quali categorie usare, mini queue, recents, ecc
                createBrowsableMediaItem(context, FOLDERS, R.string.category_folders),
                createBrowsableMediaItem(context, PLAYLISTS, R.string.category_playlists),
                createBrowsableMediaItem(context, SONGS, R.string.category_songs),
                createBrowsableMediaItem(context, ALBUMS, R.string.category_albums),
                createBrowsableMediaItem(context, ARTISTS, R.string.category_artists),
                createBrowsableMediaItem(context, GENRES, R.string.category_genres)
        )
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