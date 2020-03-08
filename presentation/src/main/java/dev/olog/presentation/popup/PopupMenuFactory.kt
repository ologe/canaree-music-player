package dev.olog.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.popup.album.AlbumPopup
import dev.olog.presentation.popup.artist.ArtistPopup
import dev.olog.presentation.popup.folder.FolderPopup
import dev.olog.presentation.popup.genre.GenrePopup
import dev.olog.presentation.popup.playlist.PlaylistPopup
import dev.olog.presentation.popup.song.SongPopup
import kotlinx.coroutines.withContext
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
    private val getPodcastAuthorUseCase: PodcastAuthorGateway,
    private val listenerFactory: MenuListenerFactory,
    private val schedulers: Schedulers

) {

    suspend fun create(anchor: View, container: View?, mediaId: MediaId): PopupMenu = withContext(schedulers.io) {
        return@withContext when (val category = mediaId.category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(anchor, container, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(anchor, container, mediaId)
            MediaIdCategory.SONGS -> getSongPopup(anchor, container, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(anchor, container, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(anchor, container, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(anchor, container, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(anchor, container, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(anchor, container, mediaId)
            MediaIdCategory.PODCASTS_AUTHOR -> getPodcastArtistPopup(anchor, container, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val folder = getFolderUseCase.getByParam(mediaId.categoryValue)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            FolderPopup(anchor, folder, song, listenerFactory.folder(container, folder, song))
        } else {
            FolderPopup(anchor, folder, null, listenerFactory.folder(container, folder, null))
        }
    }

    private fun getPlaylistPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val playlist = getPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
        } else {
            PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))
        }
    }

    private fun getSongPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val song = getSongUseCase.getByParam(mediaId.leaf!!)!!
        return SongPopup(anchor, listenerFactory.song(container, song), song)
    }

    private fun getAlbumPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val album = getAlbumUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            AlbumPopup(anchor, song, listenerFactory.album(container, album, song))
        } else {
            AlbumPopup(anchor, null, listenerFactory.album(container, album, null))
        }
    }

    private fun getArtistPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val artist = getArtistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
        } else {
            ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
        }
    }

    private fun getGenrePopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val genre = getGenreUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            GenrePopup(anchor, genre, song, listenerFactory.genre(container, genre, song))
        } else {
            GenrePopup(anchor, genre, null, listenerFactory.genre(container, genre, null))
        }
    }

    private fun getPodcastPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val song = getPodcastUseCase.getByParam(mediaId.leaf!!)!!
        return SongPopup(anchor, listenerFactory.song(container, song), song)
    }

    private fun getPodcastPlaylistPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val playlist = getPodcastPlaylistUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
        } else {
            PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))
        }
    }

    private fun getPodcastArtistPopup(anchor: View, container: View?, mediaId: MediaId): PopupMenu {
        val artist = getPodcastAuthorUseCase.getByParam(mediaId.categoryId)!!
        return if (mediaId.isLeaf) {
            val song = getSongUseCase.getByParam(mediaId.leaf!!)
            ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
        } else {
            ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
        }
    }

}