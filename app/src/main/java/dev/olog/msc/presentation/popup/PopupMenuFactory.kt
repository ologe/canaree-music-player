package dev.olog.msc.presentation.popup

import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.domain.interactor.detail.item.*
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
        private val getFolderUseCase: GetFolderUseCase,
        private val getPlaylistUseCase: GetPlaylistUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getGenreUseCase: GetGenreUseCase

){

    fun create(view: View, mediaId: MediaId): Observable<PopupMenu> {
        val category = mediaId.category
        return when (category){
            MediaIdCategory.FOLDERS -> getFolderUseCase.execute(mediaId).map { FolderPopup(view, it) }
            MediaIdCategory.PLAYLISTS -> getPlaylistUseCase.execute(mediaId).map { PlaylistPopup(view, it) }
            MediaIdCategory.SONGS -> getSongUseCase.execute(mediaId).map { SongPopup(view, it) }
            MediaIdCategory.ALBUMS -> getAlbumUseCase.execute(mediaId).map { AlbumPopup(view, it) }
            MediaIdCategory.ARTISTS -> getArtistUseCase.execute(mediaId).map { ArtistPopup(view, it) }
            MediaIdCategory.GENRES -> getGenreUseCase.execute(mediaId).map { GenrePopup(view, it) }
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

}