package dev.olog.msc.music.service.equalizer

interface IReplayGain {

    fun isImplementedByDevice(): Boolean

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

    fun setEnabled(enabled: Boolean)

}