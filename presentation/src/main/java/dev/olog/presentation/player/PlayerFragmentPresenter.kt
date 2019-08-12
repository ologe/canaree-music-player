package dev.olog.presentation.player

import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.pro.IBilling
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PlayerFragmentPresenter @Inject constructor(
    private val billing: IBilling,
    private val appPrefsUseCase: PresentationPreferencesGateway
) {

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return billing.observeBillingsState().map { it.isPremiumEnabled() }
            .combine(appPrefsUseCase.observePlayerControlsVisibility())
            { premium, show -> premium && show }
    }

}