package dev.olog.msc.music.service.helper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.all.*
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Single
import javax.inject.Inject

class MediaItemGenerator @Inject constructor(
        private val getAllFoldersUseCase: GetAllFoldersUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllGenresUseCase: GetAllGenresUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {


    fun getCategoryChilds(category: MediaIdCategory): Single<List<MediaBrowserCompat.MediaItem>> {
        return when (category){
            MediaIdCategory.FOLDERS -> getAllFoldersUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.PLAYLISTS -> getAllPlaylistsUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.SONGS -> getAllSongsUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.ALBUMS -> getAllAlbumsUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.ARTISTS -> getAllArtistsUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            MediaIdCategory.GENRES -> getAllGenresUseCase.execute().firstOrError()
                    .mapToList { it.toMediaItem() }
            else -> Single.error(IllegalArgumentException("invalid category $category"))
        }
    }

    fun getCategoryValueChilds(parentId: MediaId): Single<MutableList<MediaBrowserCompat.MediaItem>>{
        return getSongListByParamUseCase.execute(parentId)
                .firstOrError()
                .mapToList { it.toChildMediaItem(parentId) }
                .map { it.toMutableList() }

    }

    private fun Folder.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.folderId(this.path).toString())
                .setTitle(this.title)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Playlist.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.playlistId(this.id).toString())
                .setTitle(this.title)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Song.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.songId(this).toString())
                .setTitle(this.title)
                .setSubtitle(this.artist)
                .setDescription(this.album)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Song.toChildMediaItem(parentId: MediaId) : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.playableItem(parentId, this.id).toString())
                .setTitle(this.title)
                .setSubtitle(this.artist)
                .setDescription(this.album)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Album.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.albumId(this.id).toString())
                .setTitle(this.title)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Artist.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.artistId(this.id).toString())
                .setTitle(this.name)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Genre.toMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.genreId(this.id).toString())
                .setTitle(this.name)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

}