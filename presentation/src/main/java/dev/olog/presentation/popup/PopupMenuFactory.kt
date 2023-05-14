package dev.olog.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.AutoPlaylistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.presentation.popup.album.AlbumPopup
import dev.olog.presentation.popup.artist.ArtistPopup
import dev.olog.presentation.popup.folder.FolderPopup
import dev.olog.presentation.popup.genre.GenrePopup
import dev.olog.presentation.popup.playlist.AutoPlaylistPopup
import dev.olog.presentation.popup.playlist.PlaylistPopup
import dev.olog.presentation.popup.song.SongPopup
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val autoPlaylistGateway: AutoPlaylistGateway,
    private val listenerFactory: MenuListenerFactory

) {

    // TODO this is all broken, all needs parent media id
    fun create(view: View, mediaId: MediaId): PopupMenu {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS -> getSongPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.AUTO_PLAYLISTS -> getAutoPlaylistPopup(view, mediaId)
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid category $category")
        }
    }

    private fun getFolderPopup(view: View, mediaId: MediaId): PopupMenu {
        val folder = folderGateway.getById(mediaId.id)!!
        return FolderPopup(view, listenerFactory.folder(folder))
    }

    private fun getPlaylistPopup(view: View, mediaId: MediaId): PopupMenu {
        val playlist = playlistGateway.getById(mediaId.id)!!
        return PlaylistPopup(view, playlist, listenerFactory.playlist(playlist))
    }

    private fun getAutoPlaylistPopup(view: View, mediaId: MediaId): PopupMenu {
        val playlist = autoPlaylistGateway.getById(mediaId.id)!!
        return AutoPlaylistPopup(view, playlist, listenerFactory.autoPlaylist(playlist))
    }

    private fun getSongPopup(view: View, mediaId: MediaId): PopupMenu {
        val song = songGateway.getById(mediaId.id)!!
        return SongPopup(view, song, listenerFactory.song(song))
    }

    private fun getAlbumPopup(view: View, mediaId: MediaId): PopupMenu {
        val album = albumGateway.getById(mediaId.id)!!
        return AlbumPopup(view, album, listenerFactory.album(album))
    }

    private fun getArtistPopup(view: View, mediaId: MediaId): PopupMenu {
        val artist = artistGateway.getById(mediaId.id)!!
        return ArtistPopup(view, artist, listenerFactory.artist(artist))
    }

    private fun getGenrePopup(view: View, mediaId: MediaId): PopupMenu {
        val genre = genreGateway.getById(mediaId.id)!!
        return GenrePopup(view, listenerFactory.genre(genre))
    }

}