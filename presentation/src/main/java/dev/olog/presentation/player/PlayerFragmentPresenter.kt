package dev.olog.presentation.player

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.widgets.adaptive.InvalidPaletteColors
import dev.olog.shared.widgets.adaptive.InvalidProcessColors
import dev.olog.shared.widgets.adaptive.PaletteColors
import dev.olog.shared.widgets.adaptive.ProcessorColors
import dev.olog.shared.widgets.adaptive.ValidPaletteColors
import dev.olog.shared.widgets.adaptive.ValidProcessorColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerFragmentPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway
) {

    private val processorPublisher = MutableStateFlow<ProcessorColors?>(null)
    private val palettePublisher = MutableStateFlow<PaletteColors?>(null)

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return presentationPrefs.observePlayerControlsVisibility()
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {

        return processorPublisher
            .filterNotNull()
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
            .filterNotNull()
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
        processorPublisher.value = palette
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.value = palette
    }


}