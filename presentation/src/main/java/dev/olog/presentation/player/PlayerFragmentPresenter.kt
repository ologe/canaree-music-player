package dev.olog.presentation.player

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.pro.IBilling
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.widgets.adaptive.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class PlayerFragmentPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    billing: IBilling,
    presentationPrefs: PresentationPreferencesGateway
) {

    private val billingRef = WeakReference(billing)
    private val presentationPrefsRef = WeakReference(presentationPrefs)

    private val processorPublisher = BroadcastChannel<ProcessorColors>(Channel.CONFLATED)
    private val palettePublisher = BroadcastChannel<PaletteColors>(Channel.CONFLATED)

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        val billing = billingRef.get() ?: return emptyFlow()
        val presentationPrefs = presentationPrefsRef.get() ?: return emptyFlow()

        return billing.observeBillingsState().map { it.isPremiumEnabled() }
            .combine(presentationPrefs.observePlayerControlsVisibility())
            { premium, show -> premium && show }
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {
        val presentationPrefs = presentationPrefsRef.get() ?: return emptyFlow()

        return processorPublisher.asFlow()
            .map {
                val hasPlayerAppearance = context.hasPlayerAppearance()
                if (presentationPrefs.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat()) {
                    it
                } else {
                    InvalidProcessColors
                }
            }
            .filter { it is ValidProcessorColors }
            .flowOn(Dispatchers.Default)
    }

    // allow adaptive color on flat appearance
    fun observePaletteColors(): Flow<PaletteColors> {
        val presentationPrefs = presentationPrefsRef.get() ?: return emptyFlow()

        return palettePublisher
            .asFlow()
            .map {
                val hasPlayerAppearance = context.hasPlayerAppearance()
                if (presentationPrefs.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat() || hasPlayerAppearance.isSpotify()) {
                    it
                } else {
                    InvalidPaletteColors
                }
            }
            .filter { it is ValidPaletteColors }
            .flowOn(Dispatchers.Default)
    }

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.offer(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.offer(palette)
    }

}