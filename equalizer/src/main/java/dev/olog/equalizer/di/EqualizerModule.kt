package dev.olog.equalizer.di

import android.media.audiofx.AudioEffect
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.equalizer.audioeffect.AudioEffects
import dev.olog.equalizer.bassboost.BassBoostImpl
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.DynamicProcessorImpl
import dev.olog.equalizer.equalizer.EqualizerImpl
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.equalizer.virtualizer.VirtualizerImpl
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
internal class EqualizerModule {

    @Provides
    fun provideBassBoostInternal(impl: Provider<BassBoostImpl>): IBassBoost? {
        if (AudioEffects.isAvailable(AudioEffect.EFFECT_TYPE_BASS_BOOST)) {
            try {
                return impl.get()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
        return null
    }

    @Provides
    fun provideVirtualizerInternal(impl: Provider<VirtualizerImpl>): IVirtualizer? {
        if (AudioEffects.isAvailable(AudioEffect.EFFECT_TYPE_VIRTUALIZER)) {
            try {
                return impl.get()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
        return null
    }

    @Provides
    internal fun provideInternalEqualizer(
        equalizerImpl: Provider<EqualizerImpl>,
        equalizerImpl28: Provider<DynamicProcessorImpl>
    ): IEqualizer? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (AudioEffects.isAvailable(AudioEffect.EFFECT_TYPE_DYNAMICS_PROCESSING)) {
                try {
                    return equalizerImpl28.get()
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
            }
        }

        if (AudioEffects.isAvailable(AudioEffect.EFFECT_TYPE_EQUALIZER)) {
            try {
                return equalizerImpl.get()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
        return null
    }

}