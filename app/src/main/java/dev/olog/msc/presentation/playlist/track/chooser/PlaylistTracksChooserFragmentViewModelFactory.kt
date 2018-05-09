package dev.olog.msc.presentation.playlist.track.chooser

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import javax.inject.Inject

class PlaylistTracksChooserFragmentViewModelFactory @Inject constructor(
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val unsertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PlaylistTracksChooserFragmentViewModel(
                getAllSongsUseCase, unsertCustomTrackListToPlaylist
        ) as T
    }
}