package dev.olog.feature.dialog.popup

import android.view.View
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.dialog.popup.album.AlbumPopup
import dev.olog.feature.dialog.popup.artist.ArtistPopup
import dev.olog.feature.dialog.popup.folder.FolderPopup
import dev.olog.feature.dialog.popup.genre.GenrePopup
import dev.olog.feature.dialog.popup.playlist.PlaylistPopup
import dev.olog.feature.dialog.popup.track.TrackPopup
import dev.olog.navigation.PopupMenuFactory
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
        return when (mediaId) {
            is MediaId.Category -> FolderPopup(
                view = view,
                folder = folder,
                track = null,
                listener = listenerFactory.folder(folder, null)
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)
                FolderPopup(
                    view = view,
                    folder = folder,
                    track = track,
                    listener = listenerFactory.folder(folder, track)
                )
            }
        }
    }

    private suspend fun getPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = playlistGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> PlaylistPopup(
                view = view,
                playlist = playlist,
                track = null,
                listener = listenerFactory.playlist(playlist, null)
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)
                PlaylistPopup(
                    view = view,
                    playlist = playlist,
                    track = track,
                    listener = listenerFactory.playlist(playlist, track)
                )
            }
        }
    }

    private suspend fun getTrackPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        require(mediaId is MediaId.Track)
        val track = songGateway.getByParam(mediaId.id)!!
        return TrackPopup(
            view = view,
            listener = listenerFactory.track(track),
            track = track
        )
    }

    private suspend fun getAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = albumGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> AlbumPopup(
                view = view,
                track = null,
                listener = listenerFactory.album(album, null),
                tracks = { albumGateway.getTrackListByParam(mediaId.categoryValue.toLong()) })
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)!!
                AlbumPopup(
                    view = view,
                    track = track,
                    listener = listenerFactory.album(album, track),
                    tracks = { listOf(track) }
                )
            }
        }
    }

    private suspend fun getArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = artistGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> ArtistPopup(
                view = view,
                artist = artist,
                track = null,
                listener = listenerFactory.artist(artist, null),
                tracks = { artistGateway.getTrackListByParam(mediaId.categoryValue.toLong()) }
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)!!
                ArtistPopup(
                    view = view,
                    artist = artist,
                    track = track,
                    listener = listenerFactory.artist(artist, track),
                    tracks = { listOf(track) }
                )
            }
        }
    }

    private suspend fun getGenrePopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val genre = genreGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> GenrePopup(
                view = view,
                genre = genre,
                track = null,
                listener = listenerFactory.genre(genre, null)
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)
                GenrePopup(
                    view = view,
                    genre = genre,
                    track = track,
                    listener = listenerFactory.genre(genre, track)
                )
            }
        }
    }

    private suspend fun getPodcastPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        require(mediaId is MediaId.Track)
        val track = podcastGateway.getByParam(mediaId.id)!!
        return TrackPopup(
            view = view,
            listener = listenerFactory.track(track),
            track = track
        )
    }

    private suspend fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val playlist = podcastPlaylistGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> PlaylistPopup(
                view = view,
                playlist = playlist,
                track = null,
                listener = listenerFactory.playlist(playlist, null)
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)
                PlaylistPopup(
                    view = view,
                    playlist = playlist,
                    track = track,
                    listener = listenerFactory.playlist(playlist, track)
                )
            }
        }
    }

    private suspend fun getPodcastAlbumPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val album = podcastAlbumGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> AlbumPopup(
                view = view,
                track = null,
                listener = listenerFactory.album(album, null),
                tracks = { podcastAlbumGateway.getTrackListByParam(mediaId.categoryValue.toLong()) }
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)!!
                AlbumPopup(
                    view = view,
                    track = track,
                    listener = listenerFactory.album(album, track),
                    tracks = { listOf(track) }
                )
            }
        }
    }

    private suspend fun getPodcastArtistPopup(view: View, mediaId: MediaId): CascadePopupMenu {
        val artist = podcastArtistGateway.getByParam(mediaId.categoryValue.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> ArtistPopup(
                view = view,
                artist = artist,
                track = null,
                listener = listenerFactory.artist(artist, null),
                tracks = { podcastArtistGateway.getTrackListByParam(mediaId.categoryValue.toLong()) }
            )
            is MediaId.Track -> {
                val track = songGateway.getByParam(mediaId.id)!!
                ArtistPopup(
                    view = view,
                    artist = artist,
                    track = track,
                    listener = listenerFactory.artist(artist, track),
                    tracks = { listOf(track) }
                )
            }
        }
    }

}