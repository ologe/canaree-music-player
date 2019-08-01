package dev.olog.equalizer

import android.os.Build
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.equalizer.impl.BassBoostImpl
import dev.olog.equalizer.impl.EqualizerImpl
import dev.olog.equalizer.impl.EqualizerImpl28
import dev.olog.equalizer.impl.VirtualizerImpl
import javax.inject.Singleton

@Module
abstract class EqualizerModule {

    @Binds
    @Singleton
    internal abstract fun provideBassBoost(bassBoostImpl: BassBoostImpl): IBassBoost

    @Binds
    @Singleton
    internal abstract fun provideVirtualizer(virtualizerIml: VirtualizerImpl): IVirtualizer

    @Module
    companion object {

        @Provides
        @JvmStatic
        @Singleton
        internal fun provideEqualizer(
            equalizerImpl: Lazy<EqualizerImpl>,
            equalizerImpl28: Lazy<EqualizerImpl28>
        ): IEqualizer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return equalizerImpl28.get()
            }
            return equalizerImpl.get()
        }

    }

}