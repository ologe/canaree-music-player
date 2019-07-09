package dev.olog.service.music.helper

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.dagger.ApplicationContext
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.image.provider.getCachedBitmap
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

class MediaItemGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {


    suspend fun getCategoryChilds(category: MediaIdCategory): MutableList<MediaBrowserCompat.MediaItem> {
        assertBackgroundThread()
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

    suspend fun getCategoryValueChilds(parentId: MediaId): MutableList<MediaBrowserCompat.MediaItem> {
        return getSongListByParamUseCase(parentId)
            .map { it.toChildMediaItem(parentId) }
            .toMutableList()
    }

    private suspend fun Folder.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setIconBitmap(context.getCachedBitmap(getMediaId()))
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private suspend fun Playlist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setIconBitmap(context.getCachedBitmap(getMediaId()))
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private suspend fun Song.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private suspend fun Song.toChildMediaItem(parentId: MediaId): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.playableItem(parentId, this.id).toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .setIconBitmap(context.getCachedBitmap(getMediaId()))
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private suspend fun Album.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setIconBitmap(context.getCachedBitmap(getMediaId(), 200))
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private suspend fun Artist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.name)
            .setIconBitmap(context.getCachedBitmap(getMediaId()))
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    private suspend fun Genre.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.name)
            .setIconBitmap(context.getCachedBitmap(getMediaId()))
            .build()
        return MediaBrowserCompat.MediaItem(
            description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

}