package dev.olog.presentation.popup

import android.view.View
import dev.olog.core.entity.track.*
import dev.olog.presentation.popup.album.AlbumPopupListener
import dev.olog.presentation.popup.artist.ArtistPopupListener
import dev.olog.presentation.popup.folder.FolderPopupListener
import dev.olog.presentation.popup.genre.GenrePopupListener
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
    private val genrePopupListener: Provider<GenrePopupListener>
) {

    fun folder(
        container: View?,
        folder: Folder,
        song: Song?
    ) = folderPopupListener.get().setData(container, folder, song)

    fun playlist(
        container: View?,
        playlist: Playlist,
        song: Song?
    ) = playlistPopupListener.get().setData(container, playlist, song)

    fun song(
        container: View?,
        song: Song
    ) = songPopupListener.get().setData(container, song)

    fun album(
        container: View?,
        album: Album,
        song: Song?
    ) = albumPopupListener.get().setData(container, album, song)

    fun artist(
        container: View?,
        artist: Artist,
        song: Song?
    ) = artistPopupListener.get().setData(container, artist, song)

    fun genre(
        container: View?,
        genre: Genre,
        song: Song?
    ) = genrePopupListener.get().setData(container, genre, song)

}