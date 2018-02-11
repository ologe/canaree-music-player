package dev.olog.msc.domain.interactor.detail

import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Observable
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun execute(): Observable<BooleanArray> {
        return gateway.getVisibleTabs()
    }

}