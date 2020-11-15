package dev.olog.shared.android.theme

interface PlayerAppearanceAmbient {
    val value: PlayerAppearance

    fun isDefault() = value == PlayerAppearance.DEFAULT
    fun isFlat() = value == PlayerAppearance.FLAT
    fun isSpotify() = value == PlayerAppearance.SPOTIFY
    fun isFullscreen() = value == PlayerAppearance.FULLSCREEN
    fun isBigImage() = value == PlayerAppearance.BIG_IMAGE
    fun isClean() = value == PlayerAppearance.CLEAN
    fun isMini() = value == PlayerAppearance.MINI
}

enum class PlayerAppearance {
    DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;
}