package dev.olog.feature.equalizer.impl.virtualizer

interface IVirtualizerInternal {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun onDestroy()

}