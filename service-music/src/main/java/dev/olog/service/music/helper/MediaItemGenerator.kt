package dev.olog.service.music.helper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.*
import dev.olog.core.interactor.ObserveSongListByParamUseCase
import dev.olog.shared.extensions.mapToList
import io.reactivex.Single
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class MediaItemGenerator @Inject constructor(
        private val folderGateway: FolderGateway,
        private val getAllPlaylistsUseCase: PlaylistGateway,
        private val getAllSongsUseCase: SongGateway,
        private val getAllAlbumsUseCase: AlbumGateway,
        private val getAllArtistsUseCase: ArtistGateway,
        private val getAllGenresUseCase: GenreGateway,
        private val getSongListByParamUseCase: ObserveSongListByParamUseCase
) {


    fun getCategoryChilds(category: MediaIdCategory): Single<List<MediaBrowserCompat.MediaItem>> {
        return when (category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.PLAYLISTS -> getAllPlaylistsUseCase.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.SONGS -> getAllSongsUseCase.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.ALBUMS -> getAllAlbumsUseCase.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.ARTISTS -> getAllArtistsUseCase.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.GENRES -> getAllGenresUseCase.observeAll().asFlowable().firstOrError()
                    .mapToList { it.toMediaItem() }
            else -> Single.error(IllegalArgumentException("invalid category $category"))
        }
    }

    fun getCategoryValueChilds(parentId: MediaId): Single<MutableList<MediaBrowserCompat.MediaItem>> {
        return getSongListByParamUseCase(parentId)
                .asFlowable()
                .firstOrError()
                .mapToList { it.toChildMediaItem(parentId) }
                .map { it.toMutableList() }

    }

    private fun Folder.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(getMediaId().toString())
                .setTitle(this.title)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Playlist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(getMediaId().toString())
                .setTitle(this.title)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
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
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Artist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(getMediaId().toString())
                .setTitle(this.name)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Genre.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(getMediaId().toString())
                .setTitle(this.name)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

}