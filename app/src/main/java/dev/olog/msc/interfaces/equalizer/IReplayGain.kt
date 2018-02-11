package dev.olog.shared_android.interfaces.equalizer

interface IReplayGain {

    fun isImplementedByDevice(): Boolean

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

    fun setEnabled(enabled: Boolean)

}