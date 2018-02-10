package dev.olog.msc.presentation.related.artists

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.presentation.fragment_related_artist.RelatedArtistViewModel
import javax.inject.Inject

class RelatedArtistFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelatedArtistViewModel(
                application,
                mediaId,
                getSongListByParamUseCase,
                getArtistUseCase
        ) as T
    }
}