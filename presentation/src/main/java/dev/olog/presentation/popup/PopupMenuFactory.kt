package dev.olog.presentation.popup

import android.view.View
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.presentation.popup.album.AlbumPopup
import dev.olog.presentation.popup.artist.ArtistPopup
import dev.olog.presentation.popup.folder.FolderPopup
import dev.olog.presentation.popup.genre.GenrePopup
import dev.olog.presentation.popup.playlist.PlaylistPopup
import dev.olog.presentation.popup.track.SongPopup
import me.saket.cascade.CascadePopupMenu
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
    private val getFolderUseCase: FolderGateway,
    private val getPlaylistUseCase: PlaylistGateway,
    private val getSongUseCase: SongGateway,
    private val getAlbumUseCase: AlbumGateway,
    private val getArtistUseCase: ArtistGateway,
    private val getGenreUseCase: GenreGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getPodcastPlaylistUseCase: PodcastPlaylistGateway,
    private val getPodcastAlbumUseCase: PodcastAlbumGateway,
    private val getPodcastArtistUseCase: PodcastArtistGateway,
    private val listenerFactory: MenuListenerFactory

) {

    suspend fun create(view: View, mediaId: MediaId): CascadePopupMenu {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS -> getTrackPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(view, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistPopup(view, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private suspend fun getFolderPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val folder = getFolderUseCase.getByParam(mediaId.categoryValue)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            FolderPopup(view, folder, track, listenerFactory.folder(folder, track))
        } else {
            FolderPopup(view, folder, null, listenerFactory.folder(folder, null))
        }
    }

    private suspend fun getPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = getPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            PlaylistPopup(view, playlist, track, listenerFactory.playlist(playlist, track))
        } else {
            PlaylistPopup(view, playlist, null, listenerFactory.playlist(playlist, null))
        }
    }

    private suspend fun getTrackPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val track = getSongUseCase.getByParam(mediaId.leaf!!)!!
        return SongPopup(view, listenerFactory.track(track))
    }

    private suspend fun getAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = getAlbumUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            AlbumPopup(view, track, listenerFactory.album(album, track))
        } else {
            AlbumPopup(view, null, listenerFactory.album(album, null))
        }
    }

    private suspend fun getArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = getArtistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            ArtistPopup(view, artist, track, listenerFactory.artist(artist, track))
        } else {
            ArtistPopup(view, artist, null, listenerFactory.artist(artist, null))
        }
    }

    private suspend fun getGenrePopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val genre = getGenreUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            GenrePopup(view, genre, track, listenerFactory.genre(genre, track))
        } else {
            GenrePopup(view, genre, null, listenerFactory.genre(genre, null))
        }
    }

    private suspend fun getPodcastPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val track = getPodcastUseCase.getByParam(mediaId.leaf!!)!!
        return SongPopup(view, listenerFactory.track(track))
    }

    private suspend fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = getPodcastPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            PlaylistPopup(view, playlist, track, listenerFactory.playlist(playlist, track))
        } else {
            PlaylistPopup(view, playlist, null, listenerFactory.playlist(playlist, null))
        }
    }

    private suspend fun getPodcastAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = getPodcastAlbumUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            AlbumPopup(view, track, listenerFactory.album(album, track))
        } else {
            AlbumPopup(view, null, listenerFactory.album(album, null))
        }
    }

    private suspend fun getPodcastArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = getPodcastArtistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = getSongUseCase.getByParam(mediaId.leaf!!)
            ArtistPopup(view, artist, track, listenerFactory.artist(artist, track))
        } else {
            ArtistPopup(view, artist, null, listenerFactory.artist(artist, null))
        }
    }

}