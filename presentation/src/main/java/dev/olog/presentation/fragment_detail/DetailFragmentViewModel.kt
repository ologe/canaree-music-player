package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDetailDisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

class DetailFragmentViewModel(
        siblingMediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        getSongListByParamUseCase: GetSongListByParamUseCase

) : ViewModel() {

    private val category = MediaIdHelper.extractCategory(siblingMediaId)

    val itemLiveData: LiveData<DisplayableItem> = item[category]!!.asLiveData()

    val albumsLiveData : LiveData<List<DisplayableItem>> = data[category]!!.asLiveData()

    val songsLiveData: LiveData<List<DisplayableItem>> = getSongListByParamUseCase
            .execute(siblingMediaId)
            .flatMapSingle { it.toFlowable().map { it.toDetailDisplayableItem() }.toList() }
            .asLiveData()


}