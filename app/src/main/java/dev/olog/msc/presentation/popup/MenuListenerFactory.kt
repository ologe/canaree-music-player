package dev.olog.msc.presentation.popup

import dagger.Lazy
import dev.olog.msc.domain.entity.*
import dev.olog.msc.presentation.popup.album.AlbumPopupListener
import dev.olog.msc.presentation.popup.artist.ArtistPopupListener
import dev.olog.msc.presentation.popup.folder.FolderPopupListener
import dev.olog.msc.presentation.popup.genre.GenrePopupListener
import dev.olog.msc.presentation.popup.playlist.PlaylistPopupListener
import dev.olog.msc.presentation.popup.song.SongPopupListener
import javax.inject.Inject

class MenuListenerFactory @Inject constructor(
        private val folderPopupListener: Lazy<FolderPopupListener>,
        private val playlistPopupListener: Lazy<PlaylistPopupListener>,
        private val songPopupListener: Lazy<SongPopupListener>,
        private val albumPopupListener: Lazy<AlbumPopupListener>,
        private val artistPopupListener: Lazy<ArtistPopupListener>,
        private val genrePopupListener: Lazy<GenrePopupListener>
) {

    fun folder(folder: Folder, song: Song?) = folderPopupListener.get().setData(folder, song)
    fun playlist(playlist: Playlist, song: Song?) = playlistPopupListener.get().setData(playlist, song)
    fun song(song: Song) = songPopupListener.get().setData(song)
    fun album(album: Album, song: Song?) = albumPopupListener.get().setData(album, song)
    fun artist(artist: Artist, song: Song?) = artistPopupListener.get().setData(artist, song)
    fun genre(genre: Genre, song: Song?) = genrePopupListener.get().setData(genre, song)

}