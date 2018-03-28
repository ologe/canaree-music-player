package dev.olog.msc.presentation.detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.msc.domain.interactor.detail.sorting.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val item: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val albums: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val data: Map<String, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val presenter: DetailFragmentPresenter,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val getVisibleTabsUseCase : GetDetailTabsVisibilityUseCase,
        private val getDetailSortDataUseCase: GetDetailSortDataUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailFragmentViewModel(
                mediaId,
                item,
                albums,
                data,
                presenter,
                setSortOrderUseCase,
                getSortOrderUseCase,
                setSortArrangingUseCase,
                getSortArrangingUseCase,
                getVisibleTabsUseCase,
                getDetailSortDataUseCase
        ) as T
    }
}