package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.interactor.music.service.CurrentIdInPlaylistUseCase
import dev.olog.msc.domain.interactor.music.service.ObserveCurrentSongId
import dev.olog.msc.domain.interactor.music.service.ObservePlayingQueueUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.groupMap
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class PlayingQueueFragmentViewModel(
        observePlayingQueueUseCase: ObservePlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentIdInPlaylistUseCase,
        observeCurrentSongId: ObserveCurrentSongId

) : ViewModel() {

    companion object {
        var idInPlaylist = -1
    }

    val data = Flowable.merge(
            observePlayingQueueUseCase.execute().take(1),
            observePlayingQueueUseCase.execute().skip(1)
                    .debounce(500, TimeUnit.MILLISECONDS))
            .distinctUntilChanged()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .asLiveData()

    val observeCurrentSongId  = observeCurrentSongId.execute()
            .map {
                idInPlaylist = it
                it
            }.skip(1)
            .asLiveData()

    fun getCurrentSongId(): Int = currentSongIdUseCase.get()


    private fun PlayingQueueSong.toPlayingQueueDisplayableItem(): DisplayableItem {

        return DisplayableItem(
                R.layout.item_playing_queue,
                MediaId.songId(this.id),
                title,
                "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
                image,
                true,
                isRemix,
                isExplicit,
                this.idInPlaylist.toString()
        )
    }
}