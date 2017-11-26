package dev.olog.presentation.fragment_detail

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.domain.interactor.detail.most_played.InsertMostPlayedUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val siblingMediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val albums: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
        private val insertMostPlayedUseCase: InsertMostPlayedUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailFragmentViewModel(
                application,
                siblingMediaId,
                item,
                albums,
                getSongListByParamUseCase,
                getMostPlayedSongsUseCase,
                insertMostPlayedUseCase
        ) as T
    }
}