package dev.olog.equalizer

import android.os.Build
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.equalizer.bassboost.BassBoostImpl
import dev.olog.equalizer.bassboost.BassBoostProxy
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.bassboost.IBassBoostInternal
import dev.olog.equalizer.equalizer.*
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.equalizer.virtualizer.IVirtualizerInternal
import dev.olog.equalizer.virtualizer.VirtualizerImpl
import dev.olog.equalizer.virtualizer.VirtualizerProxy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EqualizerModule {

    // proxies

    @Binds
    @Singleton
    internal abstract fun provideEqualizer(impl: EqualizerProxy): IEqualizer

    @Binds
    @Singleton
    internal abstract fun provideBassBoost(impl: BassBoostProxy): IBassBoost

    @Binds
    @Singleton
    internal abstract fun provideVirtualizer(impl: VirtualizerProxy): IVirtualizer



    // implementation

    @Binds
    @Singleton
    internal abstract fun provideBassBoostInternal(impl: BassBoostImpl): IBassBoostInternal

    @Binds
    @Singleton
    internal abstract fun provideVirtualizerInternal(impl: VirtualizerImpl): IVirtualizerInternal

    companion object {

        @Provides
        internal fun provideInternalEqualizer(
            equalizerImpl: Lazy<EqualizerImpl>,
            equalizerImpl28: Lazy<EqualizerImpl28>
        ): IEqualizerInternal {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    // crashes on some devices
                    return equalizerImpl28.get()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    return equalizerImpl.get()
                }
            }
            return equalizerImpl.get()
        }

    }

}