package dev.olog.core.interactor.sort

import dev.olog.core.mediaid.MediaIdCategory
import dev.olog.core.prefs.SortPreferencesGateway
import javax.inject.Inject

class ToggleDetailSortArrangingUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway

) {

    operator fun invoke(mediaIdCategory: MediaIdCategory) {
        return gateway.toggleDetailSortArranging(mediaIdCategory)
    }
}