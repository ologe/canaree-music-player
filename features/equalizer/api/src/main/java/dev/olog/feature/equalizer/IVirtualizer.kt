package dev.olog.feature.equalizer

/**
 * Virtualizer range 0.1000
 */
interface IVirtualizer {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy(callerHash: Int)

}