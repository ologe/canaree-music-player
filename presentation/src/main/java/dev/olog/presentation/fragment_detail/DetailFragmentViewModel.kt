package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDetailDisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit

class DetailFragmentViewModel(
        siblingMediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        getSongListByParamUseCase: GetSongListByParamUseCase

) : ViewModel() {

    companion object {
        private val ONE_WEEK = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)
    }

    private val category = MediaIdHelper.extractCategory(siblingMediaId)

    val itemLiveData: LiveData<DisplayableItem> = item[category]!!.asLiveData()

    val albumsLiveData : LiveData<List<DisplayableItem>> = data[category]!!.asLiveData()

    private val sharedSongObserver = getSongListByParamUseCase
            .execute(siblingMediaId)
            .share()

    val songsLiveData: LiveData<List<DisplayableItem>> = sharedSongObserver
            .flatMapSingle { it.toFlowable().map { it.toDisplayableItem() }.toList() }
            .asLiveData()

    val recentlyAddedLiveData: LiveData<List<DisplayableItem>> = sharedSongObserver
            .filter { it.size >= 5 }
            .flatMapSingle { it.toFlowable()
                    .filter { (System.currentTimeMillis() - it.dateAdded * 1000) <= ONE_WEEK }
                    .map { it.toDetailDisplayableItem() }
                    .take(20)
                    .toList()

            }.asLiveData()

}