package dev.olog.msc.music.service.equalizer

import io.reactivex.Observable

interface IEqualizer {

    fun getBandLevel(band: Int) : Float

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun setBandLevel(band: Int, level: Float)

    fun setPreset(position: Int)

    fun getPresets(): List<String>

    fun getCurrentPreset(): Int

    fun setEnabled(enabled: Boolean)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

    fun isAvailable(): Observable<Boolean>

    interface Listener {
        fun onPresetChange(band: Int, level: Float)
    }

}