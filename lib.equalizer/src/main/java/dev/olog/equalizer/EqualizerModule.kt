package dev.olog.equalizer

import dagger.Binds
import dagger.Module
import dev.olog.equalizer.bassboost.BassBoostImpl
import dev.olog.equalizer.bassboost.BassBoostProxy
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.bassboost.IBassBoostInternal
import dev.olog.equalizer.equalizer.EqualizerProxy
import dev.olog.equalizer.equalizer.EqualizerWrapper
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.equalizer.IEqualizerInternal
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.equalizer.virtualizer.IVirtualizerInternal
import dev.olog.equalizer.virtualizer.VirtualizerImpl
import dev.olog.equalizer.virtualizer.VirtualizerProxy
import javax.inject.Singleton

@Module
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
    internal abstract fun provideBassBoostInternal(impl: BassBoostImpl): IBassBoostInternal

    @Binds
    internal abstract fun provideVirtualizerInternal(impl: VirtualizerImpl): IVirtualizerInternal

    @Binds
    internal abstract fun provideEqualizerInternal(impl: EqualizerWrapper): IEqualizerInternal

}