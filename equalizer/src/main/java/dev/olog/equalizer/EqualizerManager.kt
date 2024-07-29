package dev.olog.equalizer

import com.google.android.exoplayer2.Player
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.concurrent.withLock

@Singleton
class EqualizerManager @Inject constructor(
    private val equalizerFactory: Provider<IEqualizer?>,
    private val bassBoostFactory: Provider<IBassBoost?>,
    private val virtualizerFactory: Provider<IVirtualizer?>,
) {

    private val references = mutableMapOf<Player.Listener, Capability>()
    private val mutex = ReentrantLock(true)

    fun create(key: Player.Listener): Capability = mutex.withLock {
        require(key !in references.keys)
        Capability(
            equalizer = equalizerFactory.get(),
            bassBoost = bassBoostFactory.get(),
            virtualizer = virtualizerFactory.get(),
        ).also {
            references[key] = it
        }
    }

    fun release(key: Player.Listener) = mutex.withLock {
        references.remove(key)?.release()
    }

    fun getAll(): List<Capability> = references.values.toList()

    class Capability(
        val equalizer: IEqualizer?,
        val bassBoost: IBassBoost?,
        val virtualizer: IVirtualizer?,
    ) {

        suspend fun onAudioSessionIdChange(audioSessionId: Int) = coroutineScope {
            launch(Dispatchers.IO) { equalizer?.onAudioSessionIdChanged(audioSessionId) }
            launch(Dispatchers.IO) { bassBoost?.onAudioSessionIdChanged(audioSessionId) }
            launch(Dispatchers.IO) { virtualizer?.onAudioSessionIdChanged(audioSessionId) }
        }

        fun release() {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
        }

        fun setEnabled(enabled: Boolean) {
            equalizer?.setEnabled(enabled)
            bassBoost?.setEnabled(enabled)
            virtualizer?.setEnabled(enabled)
        }

    }

}