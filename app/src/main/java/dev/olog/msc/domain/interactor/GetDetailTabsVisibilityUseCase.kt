package dev.olog.msc.domain.interactor

import dev.olog.presentation.model.PresentationPreferencesGateway
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
    private val gateway: PresentationPreferencesGateway
) {

    fun execute(): Observable<BooleanArray> {
        return gateway.observeVisibleTabs().asObservable()
    }

}