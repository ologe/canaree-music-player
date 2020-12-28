package dev.olog.presentation.popup

import dev.olog.core.entity.track.*
import dev.olog.presentation.popup.album.AlbumPopupListener
import dev.olog.presentation.popup.artist.ArtistPopupListener
import dev.olog.presentation.popup.folder.FolderPopupListener
import dev.olog.presentation.popup.genre.GenrePopupListener
import dev.olog.presentation.popup.playlist.PlaylistPopupListener
import dev.olog.presentation.popup.track.TrackPopupListener
import javax.inject.Inject
import javax.inject.Provider

class MenuListenerFactory @Inject constructor(
    private val folderPopupListener: Provider<FolderPopupListener>,
    private val playlistPopupListener: Provider<PlaylistPopupListener>,
    private val songPopupListener: Provider<TrackPopupListener>,
    private val albumPopupListener: Provider<AlbumPopupListener>,
    private val artistPopupListener: Provider<ArtistPopupListener>,
    private val genrePopupListener: Provider<GenrePopupListener>
) {

    fun folder(folder: Folder, track: Track?) = folderPopupListener.get().setData(folder, track)
    fun playlist(playlist: Playlist, track: Track?) =
        playlistPopupListener.get().setData(playlist, track)

    fun track(track: Track) = songPopupListener.get().setData(track)
    fun album(album: Album, track: Track?) = albumPopupListener.get().setData(album, track)
    fun artist(artist: Artist, track: Track?) = artistPopupListener.get().setData(artist, track)
    fun genre(genre: Genre, track: Track?) = genrePopupListener.get().setData(genre, track)

}