package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import dev.olog.domain.interactor.MoveItemInPlaylistUseCase
import dev.olog.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortArrangingUseCase
import dev.olog.domain.interactor.detail.sorting.GetSortOrderUseCase
import dev.olog.domain.interactor.detail.sorting.SetSortArrangingUseCase
import dev.olog.domain.interactor.detail.sorting.SetSortOrderUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Flowable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val item: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val albums: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val moveItemInPlaylistUseCase: Lazy<MoveItemInPlaylistUseCase>,
        private val getVisibleTabsUseCase : GetDetailTabsVisibilityUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailFragmentViewModel(
                mediaId,
                item,
                albums,
                data,
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