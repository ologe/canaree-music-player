package dev.olog.equalizer.audioeffect

import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.UUID

object AudioEffects {

    private const val PRIORITY = Int.MAX_VALUE

    fun isAvailable(effect: UUID): Boolean {
        return try {
            AudioEffect.queryEffects().find { it.type == effect } != null
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
            false
        }
    }

    fun createEqualizer(audioSessionId: Int): Equalizer? {
        try {
            return Equalizer(PRIORITY, audioSessionId)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun createDynamicsProcessing(
        audioSessionId: Int,
        config: DynamicsProcessing.Config?
    ): DynamicsProcessing? {
        try {
            return DynamicsProcessing(PRIORITY, audioSessionId, config)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }
    }

    fun createBassBoost(audioSessionId: Int): BassBoost? {
        try {
            return BassBoost(PRIORITY, audioSessionId)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }
    }

    fun createVirtualizer(audioSessionId: Int): Virtualizer? {
        try {
            return Virtualizer(PRIORITY, audioSessionId)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }
    }

}