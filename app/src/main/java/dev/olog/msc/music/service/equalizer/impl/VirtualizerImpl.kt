package dev.olog.msc.music.service.equalizer.impl

import android.media.audiofx.Virtualizer
import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.music.service.equalizer.IVirtualizer
import dev.olog.msc.utils.k.extension.printStackTraceOnDebug
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IVirtualizer {

    private var virtualizer : Virtualizer? = null

    override fun getStrength(): Int {
        return useOrDefault({ virtualizer!!.roundedStrength.toInt() }, 0)
    }

    override fun setStrength(value: Int) {
        use {
            virtualizer!!.setStrength(value.toShort())
        }
    }

    override fun setEnabled(enabled: Boolean) {
        use {
            virtualizer!!.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        use {
            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        }

        try {
            val properties = equalizerPrefsUseCase.getVirtualizerSettings()
            val settings = Virtualizer.Settings(properties)
            virtualizer!!.properties = settings
        } catch (ex: Exception){}
    }

    override fun release() {
        virtualizer?.let {
            try {
                equalizerPrefsUseCase.saveVirtualizerSettings(it.properties.toString())
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
            ex.printStackTraceOnDebug()
        }
    }

    private fun <T> useOrDefault(action: () -> T, default: T): T {
        return try {
            action()
        } catch (ex: Exception){
            ex.printStackTraceOnDebug()
            default
        }
    }

}