package dev.olog.service.music.helper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.track.*
import dev.olog.domain.gateway.track.*
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

internal class MediaItemGenerator @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {


    suspend fun getCategoryChilds(category: MediaIdCategory): MutableList<MediaBrowserCompat.MediaItem> {
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

    private fun Track.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(getMediaId().toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Track.toChildMediaItem(parentId: MediaId): MediaBrowserCompat.MediaItem {
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