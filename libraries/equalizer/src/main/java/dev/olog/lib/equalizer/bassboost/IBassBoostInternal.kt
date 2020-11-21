package dev.olog.lib.equalizer.bassboost

/**
 * Strength range 0.1000
 */
interface IBassBoostInternal {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy()

}