package dev.olog.presentation.fragment_related_artist

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import javax.inject.Inject

class RelatedArtistViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: String,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelatedArtistViewModel(
                application,
                mediaId,
                getSongListByParamUseCase,
                getArtistUseCase
        ) as T
    }
}