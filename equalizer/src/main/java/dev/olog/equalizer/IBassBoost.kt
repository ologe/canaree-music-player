package dev.olog.equalizer

/**
 * Strength range 0.1000
 */
interface IBassBoost {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy()

}