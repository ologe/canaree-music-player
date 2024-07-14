package dev.olog.equalizer.virtualizer

interface IVirtualizer {

    fun setEnabled(enabled: Boolean)
    suspend fun onAudioSessionIdChanged(audioSessionId: Int)
    fun release()

    fun getStrength(): Int
    fun setStrength(value: Int)

}