package dev.olog.msc.presentation.related.artists

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import dev.olog.msc.domain.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.domain.interactor.detail.item.GetItemTitleUseCase
import dev.olog.msc.utils.MediaId
import java.text.Collator
import javax.inject.Inject

class RelatedArtistFragmentViewModelFactory @Inject constructor(
        private val resources: Resources,
        private val mediaId: MediaId,
        private val getRelatedArtistsUseCase: GetRelatedArtistsUseCase,
        private val getItemTitleUseCase: GetItemTitleUseCase,
        private val collator: Collator

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelatedArtistViewModel(
                resources,
                mediaId,
                getRelatedArtistsUseCase,
                getItemTitleUseCase,
                collator
        ) as T
    }
}