package dev.olog.feature.dialog.popup

import android.view.View
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.navigation.PopupMenuFactory
import dev.olog.feature.dialog.popup.album.AlbumPopup
import dev.olog.feature.dialog.popup.artist.ArtistPopup
import dev.olog.feature.dialog.popup.folder.FolderPopup
import dev.olog.feature.dialog.popup.genre.GenrePopup
import dev.olog.feature.dialog.popup.playlist.PlaylistPopup
import dev.olog.feature.dialog.popup.track.TrackPopup
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.launchUnit
import kotlinx.coroutines.withContext
import me.saket.cascade.CascadePopupMenu
import javax.inject.Inject

internal class PopupMenuFactoryImpl @Inject constructor(
    private val schedulers: Schedulers,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val listenerFactory: MenuListenerFactory
) : PopupMenuFactory {

    override fun show(
        view: View,
        mediaId: MediaId
    ) = view.viewScope.launchUnit(schedulers.cpu) {
        val popup = when (mediaId.category) {
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
        }
        withContext(schedulers.main) {
            popup.show()
        }
    }

    private suspend fun getFolderPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val folder = folderGateway.getByParam(mediaId.categoryValue)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)
            FolderPopup(view, folder, track, listenerFactory.folder(folder, track))
        } else {
            FolderPopup(view, folder, null, listenerFactory.folder(folder, null))
        }
    }

    private suspend fun getPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = playlistGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)
            PlaylistPopup(view, playlist, track, listenerFactory.playlist(playlist, track))
        } else {
            PlaylistPopup(view, playlist, null, listenerFactory.playlist(playlist, null))
        }
    }

    private suspend fun getTrackPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val track = songGateway.getByParam(mediaId.leaf!!)!!
        return TrackPopup(view, listenerFactory.track(track), track)
    }

    private suspend fun getAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = albumGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)!!
            AlbumPopup(view, track, listenerFactory.album(album, track)) {
                listOf(track)
            }
        } else {
            AlbumPopup(view, null, listenerFactory.album(album, null)) {
                albumGateway.getTrackListByParam(mediaId.categoryId)
            }
        }
    }

    private suspend fun getArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = artistGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)!!
            ArtistPopup(view, artist, track, listenerFactory.artist(artist, track)) {
                listOf(track)
            }
        } else {
            ArtistPopup(view, artist, null, listenerFactory.artist(artist, null)) {
                artistGateway.getTrackListByParam(mediaId.categoryId)
            }
        }
    }

    private suspend fun getGenrePopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val genre = genreGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)
            GenrePopup(view, genre, track, listenerFactory.genre(genre, track))
        } else {
            GenrePopup(view, genre, null, listenerFactory.genre(genre, null))
        }
    }

    private suspend fun getPodcastPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val track = podcastGateway.getByParam(mediaId.leaf!!)!!
        return TrackPopup(view, listenerFactory.track(track), track)
    }

    private suspend fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = podcastPlaylistGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)
            PlaylistPopup(view, playlist, track, listenerFactory.playlist(playlist, track))
        } else {
            PlaylistPopup(view, playlist, null, listenerFactory.playlist(playlist, null))
        }
    }

    private suspend fun getPodcastAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = podcastAlbumGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)!!
            AlbumPopup(view, track, listenerFactory.album(album, track)) {
                listOf(track)
            }
        } else {
            AlbumPopup(view, null, listenerFactory.album(album, null)) {
                podcastAlbumGateway.getTrackListByParam(mediaId.categoryId)
            }
        }
    }

    private suspend fun getPodcastArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = podcastArtistGateway.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val track = songGateway.getByParam(mediaId.leaf!!)!!
            ArtistPopup(view, artist, track, listenerFactory.artist(artist, track)) {
                listOf(track)
            }
        } else {
            ArtistPopup(view, artist, null, listenerFactory.artist(artist, null)) {
                podcastArtistGateway.getTrackListByParam(mediaId.categoryId)
            }
        }
    }

}