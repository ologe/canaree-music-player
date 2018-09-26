package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.interactor.playing.queue.ObservePlayingQueueUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.debounceFirst
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayingQueueFragmentViewModel @Inject constructor(
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        observePlayingQueueUseCase: ObservePlayingQueueUseCase

) : ViewModel() {

    val data = observePlayingQueueUseCase.execute()
            .debounceFirst()
            .distinctUntilChanged()
            .mapToList { it.toPlayingQueueDisplayableItem() }
            .asLiveData()

    val observeCurrentSongId  = musicPreferencesUseCase.observeLastIdInPlaylist()
            .skip(1)
            .asLiveData()

    fun getCurrentSongId(): Int = musicPreferencesUseCase.getLastIdInPlaylist()

    private fun PlayingQueueSong.toPlayingQueueDisplayableItem(): DisplayableItem {

        return DisplayableItem(
                R.layout.item_playing_queue,
                MediaId.songId(this.id, this.isPodcast),
                title,
                DisplayableItem.adjustArtist(artist),
                image,
                true,
                this.idInPlaylist.toString()
        )
    }
}