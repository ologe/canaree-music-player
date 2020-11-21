package dev.olog.lib.equalizer

import android.os.Build
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.lib.equalizer.bassboost.BassBoostImpl
import dev.olog.lib.equalizer.bassboost.BassBoostProxy
import dev.olog.lib.equalizer.bassboost.IBassBoost
import dev.olog.lib.equalizer.bassboost.IBassBoostInternal
import dev.olog.lib.equalizer.equalizer.*
import dev.olog.lib.equalizer.virtualizer.IVirtualizer
import dev.olog.lib.equalizer.virtualizer.IVirtualizerInternal
import dev.olog.lib.equalizer.virtualizer.VirtualizerImpl
import dev.olog.lib.equalizer.virtualizer.VirtualizerProxy
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class EqualizerModule {

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
    internal abstract fun provideBassBoostInternal(impl: BassBoostImpl): IBassBoostInternal

    @Binds
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