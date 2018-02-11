package dev.olog.msc.presentation.detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import dev.olog.msc.domain.interactor.MoveItemInPlaylistUseCase
import dev.olog.msc.domain.interactor.RemoveFromPlaylistUseCase
import dev.olog.msc.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.msc.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.msc.domain.interactor.detail.sorting.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val item: Map<MediaIdCategory, @JvmSuppressWildcards Observable<DisplayableItem>>,
        private val albums: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val data: Map<String, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val moveItemInPlaylistUseCase: Lazy<MoveItemInPlaylistUseCase>,
        private val getVisibleTabsUseCase : GetDetailTabsVisibilityUseCase,
        private val getDetailSortDataUseCase: GetDetailSortDataUseCase,
        private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase

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
                getVisibleTabsUseCase,
                getDetailSortDataUseCase,
                removeFromPlaylistUseCase
        ) as T
    }
}