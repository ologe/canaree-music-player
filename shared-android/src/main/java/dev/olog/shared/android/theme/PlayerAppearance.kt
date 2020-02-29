package dev.olog.shared.android.theme

enum class PlayerAppearance {
    DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;

    val isDefault
        get() = this == DEFAULT
    val isFlat
        get() = this == FLAT
    val isSpotify
        get() = this == SPOTIFY
    val isFullscreen
        get() = this == FULLSCREEN
    val isBigImage
        get() = this == BIG_IMAGE
    val isClean
        get() = this == CLEAN
    val isMini
        get() = this == MINI

}