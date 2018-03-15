package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class TabFragmentViewModelFactory @Inject constructor(
        private val data: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        private val appPreferencesUseCase: AppPreferencesUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabFragmentViewModel(data,
                insertLastPlayedAlbumUseCase, insertLastPlayedArtistUseCase,
                appPreferencesUseCase
        ) as T
    }
}
