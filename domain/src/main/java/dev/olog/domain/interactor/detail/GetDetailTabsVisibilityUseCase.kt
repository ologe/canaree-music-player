package dev.olog.domain.interactor.detail

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Flowable
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun execute(): Flowable<BooleanArray> {
        return gateway.getVisibleTabs()
    }

}