package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext

interface HasPlayerAppearance {
    fun playerAppearance(): PlayerAppearance

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

fun Context.hasPlayerAppearance(): HasPlayerAppearance = applicationContext.findInContext()