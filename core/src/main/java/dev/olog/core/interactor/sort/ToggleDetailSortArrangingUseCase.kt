package dev.olog.core.interactor.sort

import dev.olog.core.MediaIdCategory
import dev.olog.core.prefs.SortPreferences
import javax.inject.Inject

class ToggleDetailSortArrangingUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaIdCategory: MediaIdCategory) {
        return gateway.toggleDetailSortArranging(mediaIdCategory)
    }
}