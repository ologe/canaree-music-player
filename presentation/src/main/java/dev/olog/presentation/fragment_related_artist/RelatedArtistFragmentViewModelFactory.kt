package dev.olog.presentation.fragment_related_artist

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.GetSmallPlayType
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.shared.MediaId
import javax.inject.Inject

class RelatedArtistFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getSmallPlayType: GetSmallPlayType

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelatedArtistViewModel(
                application,
                mediaId,
                getSongListByParamUseCase,
                getArtistUseCase,
                getSmallPlayType
        ) as T
    }
}