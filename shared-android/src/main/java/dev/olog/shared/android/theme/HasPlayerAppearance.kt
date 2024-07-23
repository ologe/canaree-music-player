package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext
import kotlinx.coroutines.flow.StateFlow

interface HasPlayerAppearance {
    fun playerAppearance(): PlayerAppearance
    fun observePlayerAppearance(): StateFlow<PlayerAppearance>
}

fun PlayerAppearance.isDefault() = this == PlayerAppearance.DEFAULT
fun PlayerAppearance.isFlat() = this == PlayerAppearance.FLAT
fun PlayerAppearance.isSpotify() = this == PlayerAppearance.SPOTIFY
fun PlayerAppearance.isFullscreen() = this == PlayerAppearance.FULLSCREEN
fun PlayerAppearance.isBigImage() = this == PlayerAppearance.BIG_IMAGE
fun PlayerAppearance.isClean() = this == PlayerAppearance.CLEAN
fun PlayerAppearance.isMini() = this == PlayerAppearance.MINI

enum class PlayerAppearance {
    DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;
}

fun Context.hasPlayerAppearance(): HasPlayerAppearance = applicationContext.findInContext<HasPlayerAppearance>()