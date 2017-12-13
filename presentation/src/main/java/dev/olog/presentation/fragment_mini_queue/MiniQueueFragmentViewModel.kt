package dev.olog.presentation.fragment_mini_queue

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.music_service.GetMiniPlayingQueueUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils
import dev.olog.shared.groupMap

class MiniQueueViewModel(
        getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : ViewModel() {

    private val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, "load more id", "")

    val data: LiveData<MutableList<DisplayableItem>> = getMiniPlayingQueueUseCase
            .execute()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .map { it.toMutableList() }
            .map {
                if (it.size > 50) {
                    it[50] = footerLoadMore
                }
                it
            }.asLiveData()

}

private fun Song.toPlayingQueueDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_playing_queue_song,
            MediaIdHelper.songId(id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}