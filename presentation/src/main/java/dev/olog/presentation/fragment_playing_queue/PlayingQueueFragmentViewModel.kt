package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.interactor.music_service.CurrentIdInPlaylistUseCase
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
        private val currentSongIdUseCase: CurrentIdInPlaylistUseCase,
        observeCurrentSongId: ObserveCurrentSongId

) : ViewModel() {

    companion object {
        var idInPlaylist = -1
    }

    val data = observePlayingQueueUseCase.execute()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .asLiveData()

    val observeCurrentSongId  = observeCurrentSongId.execute()
            .map {
                PlayingQueueFragmentViewModel.idInPlaylist = it
                it
            }.skip(1)
            .asLiveData()

    fun getCurrentSongId(): Int = currentSongIdUseCase.get()


    private fun PlayingQueueSong.toPlayingQueueDisplayableItem(): DisplayableItem {

        return DisplayableItem(
                R.layout.item_playing_queue,
                MediaId.songId(this.idInPlaylist.toLong()),
                title,
                "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
                image,
                true,
                isRemix,
                isExplicit
        )
    }
}