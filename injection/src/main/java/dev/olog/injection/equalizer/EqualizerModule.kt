package dev.olog.injection.equalizer

import dagger.Binds
import dagger.Module
import dev.olog.injection.equalizer.IBassBoost
import dev.olog.injection.equalizer.IEqualizer
import dev.olog.injection.equalizer.IVirtualizer
import dev.olog.injection.equalizer.BassBoostImpl
import dev.olog.injection.equalizer.EqualizerImpl
import dev.olog.injection.equalizer.VirtualizerImpl
import javax.inject.Singleton

@Module
abstract class EqualizerModule {

    @Binds
    @Singleton
    abstract fun provideEqualizer(equalizerImpl: EqualizerImpl): IEqualizer

    @Binds
    @Singleton
    abstract fun provideBassBoost(bassBoostImpl: BassBoostImpl): IBassBoost

    @Binds
    @Singleton
    abstract fun provideVirtualizer(virtualizerIml: VirtualizerImpl): IVirtualizer

}