package dev.olog.msc.presentation.popup

import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.domain.interactor.item.*
import dev.olog.msc.presentation.popup.album.AlbumPopup
import dev.olog.msc.presentation.popup.artist.ArtistPopup
import dev.olog.msc.presentation.popup.folder.FolderPopup
import dev.olog.msc.presentation.popup.genre.GenrePopup
import dev.olog.msc.presentation.popup.playlist.PlaylistPopup
import dev.olog.msc.presentation.popup.podcast.PodcastPopup
import dev.olog.msc.presentation.popup.podcastplaylist.PodcastPlaylistPopup
import dev.olog.msc.presentation.popup.song.SongPopup
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
        private val getFolderUseCase: GetFolderUseCase,
        private val getPlaylistUseCase: GetPlaylistUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getGenreUseCase: GetGenreUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val getPodcastPlaylistUseCase: GetPodcastPlaylistUseCase,
        private val listenerFactory: MenuListenerFactory

){

    fun create(view: View, mediaId: MediaId): Single<PopupMenu> {
        val category = mediaId.category
        return when (category){
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS-> getSongPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(view, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(view, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getFolderUseCase.execute(mediaId).firstOrError()
                .flatMap { folder ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { FolderPopup(view, folder, it, listenerFactory.folder(folder, it)) }
                    } else {
                        Single.just(FolderPopup(view, folder, null, listenerFactory.folder(folder, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getPlaylistUseCase.execute(mediaId).firstOrError()
                .flatMap { playlist ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PlaylistPopup(view, playlist, it, listenerFactory.playlist(playlist, it)) }
                    } else {
                        Single.just(PlaylistPopup(view, playlist, null, listenerFactory.playlist(playlist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getSongPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getSongUseCase.execute(mediaId).firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .map { SongPopup(view, it, listenerFactory.song(it)) }

    }

    private fun getAlbumPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getAlbumUseCase.execute(mediaId).firstOrError()
                .flatMap { album ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { AlbumPopup(view, album, it, listenerFactory.album(album, it)) }
                    } else {
                        Single.just(AlbumPopup(view, album, null, listenerFactory.album(album, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getArtistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getArtistUseCase.execute(mediaId).firstOrError()
                .flatMap { artist ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { ArtistPopup(view, artist, it, listenerFactory.artist(artist, it)) }
                    } else {
                        Single.just(ArtistPopup(view, artist, null, listenerFactory.artist(artist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getGenrePopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getGenreUseCase.execute(mediaId).firstOrError()
                .flatMap { genre ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { GenrePopup(view, genre, it, listenerFactory.genre(genre, it)) }
                    } else {
                        Single.just(GenrePopup(view, genre, null, listenerFactory.genre(genre, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPodcastPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getPodcastUseCase.execute(mediaId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .map { PodcastPopup(view, it, listenerFactory.podcast(it)) }
    }

    private fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        return getPodcastPlaylistUseCase.execute(mediaId).firstOrError()
                .flatMap { playlist ->
                    if (mediaId.isLeaf){
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PodcastPlaylistPopup(view, playlist, it, listenerFactory.podcastPlaylist(playlist, it)) }
                    } else {
                        Single.just(PodcastPlaylistPopup(view, playlist, null, listenerFactory.podcastPlaylist(playlist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

}