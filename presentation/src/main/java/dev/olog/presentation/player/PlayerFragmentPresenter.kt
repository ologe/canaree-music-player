package dev.olog.presentation.player

import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.pro.IBilling
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class PlayerFragmentPresenter @Inject constructor(
    billing: IBilling,
    private val appPrefsUseCase: PresentationPreferencesGateway
) {

    private val billingRef = WeakReference(billing)

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        val billing = billingRef.get() ?: return emptyFlow()
        return billing.observeBillingsState().map { it.isPremiumEnabled() }
            .combine(appPrefsUseCase.observePlayerControlsVisibility())
            { premium, show -> premium && show }
    }

}