package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import dev.olog.msc.domain.interactor.all.last.played.InsertLastPlayedAlbumUseCase
import dev.olog.msc.domain.interactor.all.last.played.InsertLastPlayedArtistUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class TabFragmentViewModelFactory @Inject constructor(
        private val data: Lazy<Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabFragmentViewModel(data,
                insertLastPlayedAlbumUseCase, insertLastPlayedArtistUseCase
        ) as T
    }
}
