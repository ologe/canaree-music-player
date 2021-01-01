package dev.olog.domain.interactor.sort

import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.prefs.SortPreferencesGateway
import javax.inject.Inject

@Deprecated("made an arranging for each type, also inline")
class ToggleDetailSortArrangingUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway
) {

    operator fun invoke(mediaIdCategory: MediaIdCategory) {
        return gateway.toggleDetailSortArranging(mediaIdCategory)
    }
}