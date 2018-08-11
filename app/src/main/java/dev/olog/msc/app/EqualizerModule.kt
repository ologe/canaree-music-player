package dev.olog.msc.app

import dagger.Binds
import dagger.Module
import dev.olog.msc.music.service.equalizer.IBassBoost
import dev.olog.msc.music.service.equalizer.IEqualizer
import dev.olog.msc.music.service.equalizer.IVirtualizer
import dev.olog.msc.music.service.equalizer.impl.BassBoostImpl
import dev.olog.msc.music.service.equalizer.impl.EqualizerImpl
import dev.olog.msc.music.service.equalizer.impl.VirtualizerImpl
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