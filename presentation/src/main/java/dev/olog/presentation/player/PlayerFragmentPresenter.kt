package dev.olog.presentation.player

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.widgets.adaptive.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PlayerFragmentPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    private val presentationPrefs: PresentationPreferencesGateway
) {

    private val processorPublisher = ConflatedBroadcastChannel<ProcessorColors>()
    private val palettePublisher = ConflatedBroadcastChannel<PaletteColors>()

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return presentationPrefs.observePlayerControlsVisibility()
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {

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
        processorPublisher.trySend(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.trySend(palette)
    }


}