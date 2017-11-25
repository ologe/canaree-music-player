package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDetailDisplayableItem
import dev.olog.presentation.utils.asLiveData
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit

class DetailFragmentViewModel(
        siblingMediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        getSongListByParamUseCase: GetSongListByParamUseCase

) : ViewModel() {

    private val ONE_WEEK = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)

    val itemLiveData: LiveData<DisplayableItem> = item[siblingMediaId]!!.asLiveData()

    val albumsLiveData : LiveData<List<DisplayableItem>> = data[siblingMediaId]!!.asLiveData()

    val songsLiveData: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(siblingMediaId)
            .flatMapSingle { it.toFlowable().map { it.toDetailDisplayableItem() }.toList() }
            .asLiveData()

    val recentlyAddedLiveData: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(siblingMediaId)
            .flatMapSingle { it.toFlowable()
                    .filter { (System.currentTimeMillis() - it.dateAdded) <= ONE_WEEK }
                    .map { it.toDetailDisplayableItem() }
                    .take(20)
                    .toList()
            }.asLiveData()

}