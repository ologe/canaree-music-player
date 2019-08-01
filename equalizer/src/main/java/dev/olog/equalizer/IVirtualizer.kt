package dev.olog.equalizer

/**
 * Virtualizer range 0.1000
 */
interface IVirtualizer {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy()

}