package dev.olog.presentation.player

import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.pro.IBilling
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class PlayerFragmentPresenter @Inject constructor(
    private val billing: IBilling,
    private val appPrefsUseCase: PresentationPreferencesGateway
) {

    fun observePlayerControlsVisibility(): Observable<Boolean> {
        return Observables.combineLatest(
            billing.observeBillingsState().map { it.isPremiumEnabled() },
            appPrefsUseCase.observePlayerControlsVisibility()
        ) { premium, show -> premium && show }
    }

}