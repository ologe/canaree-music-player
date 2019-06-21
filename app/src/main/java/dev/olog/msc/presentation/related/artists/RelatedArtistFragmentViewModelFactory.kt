package dev.olog.msc.presentation.related.artists

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.GetItemTitleUseCase
import dev.olog.msc.domain.interactor.all.related.artists.GetRelatedArtistsUseCase
import dev.olog.core.MediaId
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
        return RelatedArtistFragmentViewModel(
                resources,
                mediaId,
                getRelatedArtistsUseCase,
                getItemTitleUseCase,
                collator
        ) as T
    }
}