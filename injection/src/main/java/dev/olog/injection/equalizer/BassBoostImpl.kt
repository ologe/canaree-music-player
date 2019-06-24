package dev.olog.injection.equalizer

import android.media.audiofx.BassBoost
import dev.olog.core.prefs.EqualizerPreferencesGateway
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IBassBoost {

    private var bassBoost : BassBoost? = null

    override fun getStrength(): Int {
        return useOrDefault({ bassBoost!!.roundedStrength.toInt() }, 0)
    }

    override fun setStrength(value: Int) {
        use {
            bassBoost!!.setStrength(value.toShort())
        }
    }

    override fun setEnabled(enabled: Boolean) {
        use {
            bassBoost!!.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        use {
            bassBoost = BassBoost(0, audioSessionId)
            bassBoost!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        }

        try {
            val properties = equalizerPrefsUseCase.getBassBoostSettings()
            val settings = BassBoost.Settings(properties)
            bassBoost!!.properties = settings
        } catch (ex: Exception){}
    }

    override fun release() {
        bassBoost?.let {
            try {
                equalizerPrefsUseCase.saveBassBoostSettings(it.properties.toString())
            } catch (ex: Exception){}
            use {
                it.release()
            }
        }
    }

    private fun use(action: () -> Unit){
        try {
            action()
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    private fun <T> useOrDefault(action: () -> T, default: T): T {
        return try {
            action()
        } catch (ex: Exception){
            ex.printStackTrace()
            default
        }
    }

}