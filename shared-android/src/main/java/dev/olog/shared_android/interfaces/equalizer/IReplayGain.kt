package dev.olog.shared_android.interfaces.equalizer

interface IReplayGain {

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

}