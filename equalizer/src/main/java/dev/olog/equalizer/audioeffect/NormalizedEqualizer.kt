package dev.olog.equalizer.audioeffect

class NormalizedEqualizer(audioSession: Int) {

    private val equalizer = AudioEffects.createEqualizer(audioSession)

    var enabled: Boolean
        get() = equalizer?.enabled ?: false
        set(value) {
            equalizer?.enabled = value
        }

    // return frequency in Hz instead of milliHz
    fun getBandFrequency(band: Int): Float {
        val freq = equalizer?.getCenterFreq(band.toShort())?.toFloat() ?: return 0f
        return freq / 1000
    }

    // return gain in dB instead of mB
    // 1 dB -> 100 mB
    fun getBandLevel(band: Int): Float {
        val mb = equalizer?.getBandLevel(band.toShort())?.toFloat() ?: return 0f
        return mb / 100
    }

    // converts dB in Mb and updat eq
    // 1 dB -> 100 mB
    fun setBandLevel(band: Int, gain: Float) {
        val mb = gain * 100
        equalizer?.setBandLevel(band.toShort(), mb.toInt().toShort())
    }

    fun release() {
        equalizer?.release()
    }

}