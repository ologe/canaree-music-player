package dev.olog.domain.interactor.sort

import dev.olog.domain.MediaIdCategory
import dev.olog.domain.prefs.SortPreferences
import javax.inject.Inject

class ToggleDetailSortArrangingUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaIdCategory: MediaIdCategory) {
        return gateway.toggleDetailSortArranging(mediaIdCategory)
    }
}