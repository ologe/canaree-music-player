package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import dev.olog.domain.interactor.MoveItemInPlaylistUseCase
import dev.olog.domain.interactor.detail.ObserveDetailTabsVisiblityUseCase
import dev.olog.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortArrangingUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortOrderUseCase
import dev.olog.domain.interactor.detail.sorting.SetSortArrangingUseCase
import dev.olog.domain.interactor.detail.sorting.SetSortOrderUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val mediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val albums: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val moveItemInPlaylistUseCase: Lazy<MoveItemInPlaylistUseCase>,
        private val getVisibleTabsUseCase : ObserveDetailTabsVisiblityUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailFragmentViewModel(
                mediaId,
                item,
                albums,
                headers,
                getArtistFromAlbumUseCase,
                setSortOrderUseCase,
                getSortOrderUseCase,
                setSortArrangingUseCase,
                getSortArrangingUseCase,
                moveItemInPlaylistUseCase,
                getVisibleTabsUseCase
        ) as T
    }
}