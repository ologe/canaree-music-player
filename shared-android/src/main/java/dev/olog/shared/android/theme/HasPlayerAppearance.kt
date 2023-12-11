package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext
import kotlinx.coroutines.flow.StateFlow

interface HasPlayerAppearance {
    fun observePlayerAppearance(): StateFlow<PlayerAppearance>
    fun playerAppearance() = observePlayerAppearance().value

    fun isDefault() = playerAppearance() == PlayerAppearance.DEFAULT
    fun isFlat() = playerAppearance() == PlayerAppearance.FLAT
    fun isSpotify() = playerAppearance() == PlayerAppearance.SPOTIFY
    fun isFullscreen() = playerAppearance() == PlayerAppearance.FULLSCREEN
    fun isBigImage() = playerAppearance() == PlayerAppearance.BIG_IMAGE
    fun isClean() = playerAppearance() == PlayerAppearance.CLEAN
    fun isMini() = playerAppearance() == PlayerAppearance.MINI
}

enum class PlayerAppearance {
    DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;
}

fun PlayerAppearance.isDefault() = this == PlayerAppearance.DEFAULT
fun PlayerAppearance.isFlat() = this == PlayerAppearance.FLAT
fun PlayerAppearance.isSpotify() = this == PlayerAppearance.SPOTIFY
fun PlayerAppearance.isFullscreen() = this == PlayerAppearance.FULLSCREEN
fun PlayerAppearance.isBigImage() = this == PlayerAppearance.BIG_IMAGE
fun PlayerAppearance.isClean() = this == PlayerAppearance.CLEAN
fun PlayerAppearance.isMini() = this == PlayerAppearance.MINI

fun Context.hasPlayerAppearance(): HasPlayerAppearance = applicationContext.findInContext()