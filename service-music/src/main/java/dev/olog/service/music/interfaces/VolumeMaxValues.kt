package dev.olog.service.music.interfaces

internal interface VolumeMaxValues {

    val normal: Float
    val duck: Float

    fun maxVolume(isDucking: Boolean): Float {
        return if (isDucking) duck else normal
    }

}