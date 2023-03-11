package dev.olog.platform.theme

import android.content.Context
import dev.olog.platform.extension.findInContext

fun Context.hasPlayerAppearance(): HasPlayerAppearance {
    return applicationContext.findInContext()
}

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