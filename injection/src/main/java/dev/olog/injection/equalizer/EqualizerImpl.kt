package dev.olog.injection.equalizer

import android.media.audiofx.Equalizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IEqualizer {

    private var equalizer : Equalizer? = null
    private val listeners = mutableListOf<IEqualizer.Listener>()

    private val availabilityPublisher = BehaviorSubject.createDefault(true)

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float {
        return useOrDefault({ equalizer!!.getBandLevel(band.toShort()).toFloat() }, 0f)
    }

    override fun setBandLevel(band: Int, level: Float) {
        use {
            equalizer!!.setBandLevel(band.toShort(), level.toShort())
        }
    }

    override fun setPreset(position: Int) {
        use {
            equalizer!!.usePreset(position.toShort())

            listeners.forEach {
                for (band in 0 until equalizer!!.numberOfBands){
                    val level = equalizer!!.getBandLevel(band.toShort()) / 100
                    it.onPresetChange(band, level.toFloat())
                }
            }
        }
    }

    override fun getPresets(): List<String> {
        return useOrDefault({
            (0 until equalizer!!.numberOfPresets)
                    .map { equalizer!!.getPresetName(it.toShort()) }
        }, emptyList())
    }

    override fun getCurrentPreset(): Int {
        return useOrDefault({ equalizer!!.currentPreset.toInt() }, 0)
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        use {
            equalizer = Equalizer(0, audioSessionId)
            equalizer!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        }

        try {
            val properties = equalizerPrefsUseCase.getEqualizerSettings()
            val settings = Equalizer.Settings(properties)
            equalizer!!.properties = settings
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        use {
            equalizer!!.enabled = enabled
        }
    }

    override fun release() {
        equalizer?.let {
            try {
                equalizerPrefsUseCase.saveEqualizerSettings(it.properties.toString())
            } catch (ex: Exception){
                ex.printStackTrace()
            }
            use {
                it.release()
            }
        }
    }

    override fun isAvailable(): Observable<Boolean> = availabilityPublisher.distinctUntilChanged()

    private fun use(action: () -> Unit){
        try {
            action()
            availabilityPublisher.onNext(true)
        } catch (ex: Exception){
            ex.printStackTrace()
            availabilityPublisher.onNext(false)
        }
    }

    private fun <T> useOrDefault(action: () -> T, default: T): T {
        return try {
            val v = action()
            availabilityPublisher.onNext(true)
            v
        } catch (ex: Exception){
            ex.printStackTrace()
            availabilityPublisher.onNext(false)
            default
        }
    }

}