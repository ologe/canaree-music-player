package dev.olog.msc.module

import dagger.Binds
import dagger.Module
import dev.olog.music_service.equalizer.BassBoostImpl
import dev.olog.music_service.equalizer.EqualizerImpl
import dev.olog.music_service.equalizer.ReplayGainImpl
import dev.olog.music_service.equalizer.VirtualizerImpl
import dev.olog.shared_android.interfaces.equalizer.IBassBoost
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import dev.olog.shared_android.interfaces.equalizer.IReplayGain
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer
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

    @Binds
    @Singleton
    abstract fun provideReplayGain(replayGainImpl: ReplayGainImpl): IReplayGain

}