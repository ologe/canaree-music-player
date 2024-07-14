package dev.olog.equalizer.bassboost

/**
 * Strength range 0.1000
 */
interface IBassBoost {

    fun setEnabled(enabled: Boolean)
    suspend fun onAudioSessionIdChanged(audioSessionId: Int)
    fun release()

    fun getStrength(): Int
    fun setStrength(value: Int)

}