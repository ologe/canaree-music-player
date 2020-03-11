package dev.olog.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory
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
    private val getFolderUseCase: FolderGateway,
    private val getPlaylistUseCase: PlaylistGateway,
    private val getSongUseCase: SongGateway,
    private val getAlbumUseCase: AlbumGateway,
    private val getArtistUseCase: ArtistGateway,
    private val getGenreUseCase: GenreGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getPodcastPlaylistUseCase: PodcastPlaylistGateway,
    private val getPodcastAuthorUseCase: PodcastAuthorGateway,
    private val listenerFactory: MenuListenerFactory,
    private val schedulers: Schedulers

) {

    suspend fun create(anchor: View, container: View?, mediaId: PresentationId): PopupMenu = withContext(schedulers.io) {
        return@withContext when (val category = mediaId.category) {
            PresentationIdCategory.FOLDERS -> getFolderPopup(anchor, container, mediaId)
            PresentationIdCategory.PLAYLISTS -> getPlaylistPopup(anchor, container, mediaId)
            PresentationIdCategory.SONGS -> getSongPopup(anchor, container, mediaId)
            PresentationIdCategory.ALBUMS -> getAlbumPopup(anchor, container, mediaId)
            PresentationIdCategory.ARTISTS -> getArtistPopup(anchor, container, mediaId)
            PresentationIdCategory.GENRES -> getGenrePopup(anchor, container, mediaId)
            PresentationIdCategory.PODCASTS -> getPodcastPopup(anchor, container, mediaId)
            PresentationIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(anchor, container, mediaId)
            PresentationIdCategory.PODCASTS_AUTHORS -> getPodcastArtistPopup(anchor, container, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(anchor: View, container: View?, mediaId: PresentationId): FolderPopup {
        val folder = getFolderUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                FolderPopup(anchor, folder, null, listenerFactory.folder(container, folder, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                FolderPopup(anchor, folder, song, listenerFactory.folder(container, folder, song))
            }
        }
    }

    private fun getPlaylistPopup(anchor: View, container: View?, mediaId: PresentationId): PlaylistPopup {
        val playlist = getPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))

            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getSongPopup(anchor: View, container: View?, mediaId: PresentationId): SongPopup {
        return when (mediaId) {
            is PresentationId.Category -> throwNotHandled("invalid $mediaId")
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)!!
                SongPopup(anchor, listenerFactory.song(container, song), song)
            }
        }
    }

    private fun getAlbumPopup(anchor: View, container: View?, mediaId: PresentationId): AlbumPopup {
        val album = getAlbumUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                AlbumPopup(anchor, null, listenerFactory.album(container, album, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                AlbumPopup(anchor, song, listenerFactory.album(container, album, song))
            }
        }
    }

    private fun getArtistPopup(anchor: View, container: View?, mediaId: PresentationId): ArtistPopup {
        val artist = getArtistUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

    private fun getGenrePopup(anchor: View, container: View?, mediaId: PresentationId): GenrePopup {
        val genre = getGenreUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                GenrePopup(anchor, genre, null, listenerFactory.genre(container, genre, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                GenrePopup(anchor, genre, song, listenerFactory.genre(container, genre, song))
            }
        }
    }

    private fun getPodcastPopup(anchor: View, container: View?, mediaId: PresentationId): SongPopup {
        return when (mediaId) {
            is PresentationId.Category -> throwNotHandled("invalid $mediaId")
            is PresentationId.Track -> {
                val song = getPodcastUseCase.getByParam(mediaId.id)!!
                SongPopup(anchor, listenerFactory.song(container, song), song)
            }
        }

    }

    private fun getPodcastPlaylistPopup(anchor: View, container: View?, mediaId: PresentationId): PlaylistPopup {
        val playlist = getPodcastPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getPodcastArtistPopup(anchor: View, container: View?, mediaId: PresentationId): ArtistPopup {
        val artist = getPodcastAuthorUseCase.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is PresentationId.Track -> {
                val song = getSongUseCase.getByParam(mediaId.id)
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

}