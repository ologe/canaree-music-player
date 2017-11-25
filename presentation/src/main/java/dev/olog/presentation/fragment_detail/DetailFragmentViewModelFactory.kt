package dev.olog.presentation.fragment_detail

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val siblingMediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val albums: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        println("wtf?")
        return DetailFragmentViewModel(
                application,
                siblingMediaId,
                item,
                albums,
                getSongListByParamUseCase
        ) as T
    }
}