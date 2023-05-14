package dev.olog.presentation.popup

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.AutoPlaylist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.popup.album.AlbumPopupListener
import dev.olog.presentation.popup.artist.ArtistPopupListener
import dev.olog.presentation.popup.folder.FolderPopupListener
import dev.olog.presentation.popup.genre.GenrePopupListener
import dev.olog.presentation.popup.playlist.AutoPlaylistPopupListener
import dev.olog.presentation.popup.playlist.PlaylistPopupListener
import dev.olog.presentation.popup.song.SongPopupListener
import javax.inject.Inject
import javax.inject.Provider

class MenuListenerFactory @Inject constructor(
    private val folderPopupListener: Provider<FolderPopupListener>,
    private val playlistPopupListener: Provider<PlaylistPopupListener>,
    private val songPopupListener: Provider<SongPopupListener>,
    private val albumPopupListener: Provider<AlbumPopupListener>,
    private val artistPopupListener: Provider<ArtistPopupListener>,
    private val genrePopupListener: Provider<GenrePopupListener>,
    private val autoPlaylistPopupListener: Provider<AutoPlaylistPopupListener>,
) {

    fun folder(folder: Folder): FolderPopupListener {
        return folderPopupListener.get().setData(folder)
    }

    fun playlist(playlist: Playlist): PlaylistPopupListener {
        return playlistPopupListener.get().setData(playlist)
    }

    fun autoPlaylist(playlist: AutoPlaylist): AutoPlaylistPopupListener {
        return autoPlaylistPopupListener.get().setData(playlist)
    }

    fun song(song: Song): SongPopupListener {
        return songPopupListener.get().setData(song)
    }

    fun album(album: Album): AlbumPopupListener {
        return albumPopupListener.get().setData(album)
    }

    fun artist(artist: Artist): ArtistPopupListener {
        return artistPopupListener.get().setData(artist)
    }

    fun genre(genre: Genre): GenrePopupListener {
        return genrePopupListener.get().setData(genre)
    }

}