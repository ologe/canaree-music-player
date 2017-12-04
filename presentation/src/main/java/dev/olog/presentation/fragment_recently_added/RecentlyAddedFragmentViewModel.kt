package dev.olog.presentation.fragment_recently_added

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import io.reactivex.rxkotlin.toFlowable

class RecentlyAddedFragmentViewModel(
        mediaId: String,
        useCase: GetRecentlyAddedUseCase

) : ViewModel() {

    val data : LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .flatMapSingle { it.toFlowable().map { it.toDisplayableItem() }.toList() }
            .asLiveData()

}