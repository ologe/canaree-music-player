package dev.olog.equalizer.audioeffect

import android.media.audiofx.Equalizer

class NormalizedEqualizer(priority: Int, audioSession: Int) {

    private val equalizer = Equalizer(priority, audioSession)

    var enabled: Boolean
        get() = equalizer.enabled
        set(value) {
            equalizer.enabled = value
        }

    init {
        for (index in 0 until equalizer.numberOfPresets) {
            val presetName = equalizer.getPresetName(index.toShort())
            println("preset $presetName")
            equalizer.usePreset(index.toShort())

            for (band in 0 until equalizer.numberOfBands) {
                println("${getBandFrequency(band)}:${getBandLevel(band )}")
            }
        }
    }

    // return frequency in Hz instead of milliHz
    fun getBandFrequency(band: Int): Float {
        val freq = equalizer.getCenterFreq(band.toShort()).toFloat()
        return freq / 1000
    }

    // return gain in dB instead of mB
    // 1 dB -> 100 mB
    fun getBandLevel(band: Int): Float {
        val mb = equalizer.getBandLevel(band.toShort()).toFloat()
        return mb / 100
    }

    // converts dB in Mb and updat eq
    // 1 dB -> 100 mB
    fun setBandLevel(band: Int, gain: Float) {
        val mb = gain * 100
        equalizer.setBandLevel(band.toShort(), mb.toShort())
    }

    fun release() {
        equalizer.release()
    }

}