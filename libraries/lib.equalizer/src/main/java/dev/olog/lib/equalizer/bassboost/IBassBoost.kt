package dev.olog.lib.equalizer.bassboost

/**
 * Strength range 0.1000
 */
interface IBassBoost {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy(callerHash: Int)

}