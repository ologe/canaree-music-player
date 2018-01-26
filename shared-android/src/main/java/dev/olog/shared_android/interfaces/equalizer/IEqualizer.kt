package dev.olog.shared_android.interfaces.equalizer

interface IEqualizer {

    fun getBandLevel(band: Int) : Float

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun setBandLevel(band: Int, level: Float)

    fun setPreset(position: Int)

    fun getPresets(): List<String>

    fun getCurrentPreset(): Int

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun release()

    interface Listener {
        fun onPresetChange(band: Int, level: Float)
    }

}