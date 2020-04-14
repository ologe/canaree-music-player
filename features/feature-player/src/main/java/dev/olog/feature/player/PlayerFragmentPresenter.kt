package dev.olog.feature.player

import android.content.Context
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.player.prefs.PlayerPreferences
import dev.olog.feature.presentation.base.widget.adaptive.*
import dev.olog.shared.android.theme.themeManager
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PlayerFragmentPresenter @Inject constructor(
    private val context: Context,
    private val preferences: PlayerPreferences,
    private val schedulers: Schedulers
) {

    private val processorPublisher = ConflatedBroadcastChannel<ProcessorColors>()
    private val palettePublisher = ConflatedBroadcastChannel<PaletteColors>()

    fun observePlayerControlsVisibility(): Flow<Boolean> {

        return preferences.observePlayerControlsVisibility()
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {

        return processorPublisher.asFlow()
            .map {
                val hasPlayerAppearance = context.themeManager.playerAppearance
                if (preferences.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat) {
                    it
                } else {
                    InvalidProcessColors
                }
            }
            .filter { it is ValidProcessorColors }
            .flowOn(schedulers.cpu)
    }

    // allow adaptive color on flat appearance
    fun observePaletteColors(): Flow<PaletteColors> {

        return palettePublisher
            .asFlow()
            .map {
                val hasPlayerAppearance = context.themeManager.playerAppearance
                if (preferences.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat || hasPlayerAppearance.isSpotify) {
                    it
                } else {
                    InvalidPaletteColors
                }
            }
            .filter { it is ValidPaletteColors }
            .flowOn(schedulers.cpu)
    }

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.offer(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.offer(palette)
    }


}