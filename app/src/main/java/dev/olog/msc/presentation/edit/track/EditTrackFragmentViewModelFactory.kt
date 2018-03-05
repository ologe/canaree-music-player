package dev.olog.msc.presentation.edit.track

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackUseCase
import dev.olog.msc.domain.interactor.song.image.DeleteSongImageUseCase
import dev.olog.msc.domain.interactor.song.image.InsertSongImageUseCase
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditTrackFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getSongUseCase: GetSongUseCase,
        private val insertSongImageUseCase: InsertSongImageUseCase,
        private val deleteSongImageUseCase: DeleteSongImageUseCase,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val getLastFmTrackUseCase: GetLastFmTrackUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditTrackFragmentViewModel(
                application, mediaId,
                getSongUseCase,
                insertSongImageUseCase,
                deleteSongImageUseCase,
                connectionPublisher,
                getLastFmTrackUseCase
        ) as T
    }
}