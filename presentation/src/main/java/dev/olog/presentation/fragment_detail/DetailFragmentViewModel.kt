package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDetailDisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

class DetailFragmentViewModel(
        private val siblingMediaId: String,
        private val data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ViewModel() {

    private val category = MediaIdHelper.extractCategory(siblingMediaId)

    val siblingsObservable : Flowable<List<DisplayableItem>>
        get() = data[category]!!.replay(1).refCount()

    val mediaIdLiveData = MutableLiveData<String>()

    init {
        mediaIdLiveData.value = siblingMediaId
    }

    val songListLiveData = Transformations.switchMap(mediaIdLiveData, { input ->
        val source = MediaIdHelper.mapCategoryToSource(siblingMediaId)
        getSongListByParamUseCase.execute(input)
                .flatMapSingle { it.toFlowable()
                        .map { it.toDetailDisplayableItem(source) }
                        .toList()
                }.asLiveData()
    })

    fun onMediaItemChanged(mediaItem: DisplayableItem) {
        mediaIdLiveData.value = mediaItem.mediaId
//        headerLiveData.value = mediaItem.title
    }



}