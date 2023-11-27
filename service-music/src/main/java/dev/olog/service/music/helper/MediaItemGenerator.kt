package dev.olog.service.music.helper

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

internal class MediaItemGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {


    fun getCategoryChilds(category: MediaIdCategory): MutableList<MediaBrowserCompat.MediaItem> {
        return when (category) {
            MediaIdCategory.FOLDERS -> folderGateway.getAll().map { it.toMediaItem() }
            MediaIdCategory.PLAYLISTS -> playlistGateway.getAll().map { it.toMediaItem() }
            MediaIdCategory.SONGS -> songGateway.getAll().map { it.toMediaItem() }
            MediaIdCategory.ALBUMS -> albumGateway.getAll().map { it.toMediaItem() }
            MediaIdCategory.ARTISTS -> artistGateway.getAll().map { it.toMediaItem() }
            MediaIdCategory.GENRES -> genreGateway.getAll().map { it.toMediaItem() }
            else -> throw IllegalArgumentException("invalid category $category")
        }.toMutableList()
    }

    fun getCategoryValueChilds(parentId: MediaId): MutableList<MediaBrowserCompat.MediaItem> {
        return getSongListByParamUseCase(parentId)
            .map { it.toChildMediaItem(parentId) }
            .toMutableList()
    }

    private fun Folder.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private fun Playlist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private fun Song.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Song.toChildMediaItem(parentId: MediaId): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.playableItem(parentId, this.id).toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Album.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private fun Artist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.name)
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private fun Genre.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.name)
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

}