package dev.olog.presentation.fragment_queue

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.service.GetMiniPlayingQueueUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import io.reactivex.rxkotlin.toFlowable

class PlayingQueueViewModel(
        getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : ViewModel() {

    private val footerLoadMore = DisplayableItem(R.layout.item_load_more, "load more id", "")

    val data: LiveData<List<DisplayableItem>> = getMiniPlayingQueueUseCase
            .execute()
            .flatMapSingle { it.toFlowable().map { it.toDisplayableItem() }.toList() }
            .map {
                if (it.size > 50) {
                    it[50] = footerLoadMore
                }
                it
            }.asLiveData()

}