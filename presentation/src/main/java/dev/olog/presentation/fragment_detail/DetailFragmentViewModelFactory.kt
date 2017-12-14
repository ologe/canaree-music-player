package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val mediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val albums: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailFragmentViewModel(
                mediaId,
                item,
                albums,
                headers,
                getArtistFromAlbumUseCase
        ) as T
    }
}