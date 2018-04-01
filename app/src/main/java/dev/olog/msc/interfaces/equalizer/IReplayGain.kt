package dev.olog.msc.interfaces.equalizer

interface IReplayGain {

    fun isImplementedByDevice(): Boolean

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

    fun setEnabled(enabled: Boolean)

}