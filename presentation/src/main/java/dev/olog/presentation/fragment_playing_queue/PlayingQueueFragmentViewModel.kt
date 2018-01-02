package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.music_service.CurrentSongIdUseCase
import dev.olog.domain.interactor.music_service.ObserveCurrentSongId
import dev.olog.domain.interactor.music_service.ObservePlayingQueueUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.extension.asLiveData

class PlayingQueueFragmentViewModel(
        observePlayingQueueUseCase: ObservePlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentSongIdUseCase,
        observeCurrentSongId: ObserveCurrentSongId

) : ViewModel() {

    companion object {
        var songId = -1L
    }

    val data = observePlayingQueueUseCase.execute()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .asLiveData()

    val observeCurrentSongId  = observeCurrentSongId.execute()
            .map {
                PlayingQueueFragmentViewModel.songId = it
                it
            }.skip(1)
            .asLiveData()

    fun getCurrentSongId(): Long = currentSongIdUseCase.get()


    private fun Song.toPlayingQueueDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_playing_queue,
                MediaId.songId(id),
                title,
                "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
                image,
                true,
                isRemix,
                isExplicit
        )
    }
}