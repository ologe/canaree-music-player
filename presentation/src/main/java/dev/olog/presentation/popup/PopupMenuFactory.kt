package dev.olog.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.schedulers.Schedulers
import dev.olog.presentation.popup.album.AlbumPopup
import dev.olog.presentation.popup.artist.ArtistPopup
import dev.olog.presentation.popup.folder.FolderPopup
import dev.olog.presentation.popup.genre.GenrePopup
import dev.olog.presentation.popup.playlist.PlaylistPopup
import dev.olog.presentation.popup.song.SongPopup
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PopupMenuFactory @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway,
    private val listenerFactory: MenuListenerFactory,
    private val schedulers: Schedulers

) {

    suspend fun create(anchor: View, container: View?, mediaId: MediaId): PopupMenu = withContext(schedulers.io) {
        return@withContext when (val category = mediaId.category) {
            FOLDERS -> getFolderPopup(anchor, container, mediaId)
            PLAYLISTS -> getPlaylistPopup(anchor, container, mediaId)
            SONGS,
            PODCASTS -> getSongPopup(anchor, container, mediaId)
            ALBUMS -> getAlbumPopup(anchor, container, mediaId)
            ARTISTS -> getArtistPopup(anchor, container, mediaId)
            GENRES -> getGenrePopup(anchor, container, mediaId)
            PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(anchor, container, mediaId)
            PODCASTS_AUTHORS -> getPodcastArtistPopup(anchor, container, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(anchor: View, container: View?, mediaId: MediaId): FolderPopup {
        val folder = folderGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is MediaId.Category -> {
                FolderPopup(anchor, folder, null, listenerFactory.folder(container, folder, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                FolderPopup(anchor, folder, song, listenerFactory.folder(container, folder, song))
            }
        }
    }

    private fun getPlaylistPopup(anchor: View, container: View?, mediaId: MediaId): PlaylistPopup {
        val playlist = playlistGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))

            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getSongPopup(anchor: View, container: View?, mediaId: MediaId): SongPopup {
        return when (mediaId) {
            is MediaId.Category -> throwNotHandled(mediaId)
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())!!
                SongPopup(anchor, listenerFactory.song(container, song), song)
            }
        }
    }

    private fun getAlbumPopup(anchor: View, container: View?, mediaId: MediaId): AlbumPopup {
        val album = albumGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                AlbumPopup(anchor, null, listenerFactory.album(container, album, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                AlbumPopup(anchor, song, listenerFactory.album(container, album, song))
            }
        }
    }

    private fun getArtistPopup(anchor: View, container: View?, mediaId: MediaId): ArtistPopup {
        val artist = artistGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

    private fun getGenrePopup(anchor: View, container: View?, mediaId: MediaId): GenrePopup {
        val genre = genreGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                GenrePopup(anchor, genre, null, listenerFactory.genre(container, genre, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                GenrePopup(anchor, genre, song, listenerFactory.genre(container, genre, song))
            }
        }
    }

    private fun getPodcastPlaylistPopup(anchor: View, container: View?, mediaId: MediaId): PlaylistPopup {
        val playlist = podcastPlaylistGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getPodcastArtistPopup(anchor: View, container: View?, mediaId: MediaId): ArtistPopup {
        val artist = podcastAuthorGateway.getByParam(mediaId.categoryId.toLong())!!
        return when (mediaId) {
            is MediaId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is MediaId.Track -> {
                val song = trackGateway.getByParam(mediaId.id.toLong())
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

}