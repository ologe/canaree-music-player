package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.music_service.ObservePlayingQueueUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toPlayerMetadata
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.rx.groupMap
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

class PlayingQueueFragmentViewModel(
        observePlayingQueueUseCase: ObservePlayingQueueUseCase,
        controllerCallback: RxMusicServiceControllerCallback

) : ViewModel() {

    val metadata = controllerCallback.onMetadataChanged()
            .map { it.toPlayerMetadata() }
            .distinctUntilChanged()
            .asLiveData()

    val data = observePlayingQueueUseCase.execute()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .asLiveData()

}

private fun Song.toPlayingQueueDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_playing_queue,
            MediaIdHelper.songId(id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}