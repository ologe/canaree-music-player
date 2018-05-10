package dev.olog.msc.music.service.equalizer

interface IVirtualizer {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun release()

}