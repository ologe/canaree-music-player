package dev.olog.feature.player.player

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.widgets.adaptive.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PlayerFragmentPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPrefs: AppPreferencesGateway
) {

    private val processorPublisher = MutableStateFlow<ProcessorColors?>(null)
    private val palettePublisher = MutableStateFlow<PaletteColors?>(null)

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return appPrefs.observePlayerControlsVisibility()
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {

        return processorPublisher
            .filterNotNull()
            .map {
                val playerAppearanceAmbient = context.playerAppearanceAmbient
                if (appPrefs.isAdaptiveColorEnabled || playerAppearanceAmbient.isFlat()) {
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
                val playerAppearanceAmbient = context.playerAppearanceAmbient
                if (appPrefs.isAdaptiveColorEnabled || playerAppearanceAmbient.isFlat() || playerAppearanceAmbient.isSpotify()) {
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