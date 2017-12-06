package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class TabFragmentViewModelFactory @Inject constructor(
        private val data: Map<Int, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabFragmentViewModel(data,
                insertLastPlayedAlbumUseCase, insertLastPlayedArtistUseCase,
                getAlbumUseCase, getArtistUseCase
        ) as T
    }
}
