package dev.olog.msc.presentation.player

import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.pro.IBilling
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class PlayerFragmentPresenter @Inject constructor(
    private val billing: IBilling,
    private val appPrefsUseCase: AppPreferencesGateway
) {

    fun observePlayerControlsVisibility(): Observable<Boolean> {
        return Observables.combineLatest(
            billing.observeBillingsState().map { it.isPremiumEnabled() },
            appPrefsUseCase.observePlayerControlsVisibility()
        ) { premium, show -> premium && show }
    }

}