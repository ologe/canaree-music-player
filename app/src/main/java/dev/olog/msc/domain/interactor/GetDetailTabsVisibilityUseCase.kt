package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import io.reactivex.Observable
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
    private val gateway: PresentationPreferences
) {

    fun execute(): Observable<BooleanArray> {
        return gateway.observeVisibleTabs()
    }

}