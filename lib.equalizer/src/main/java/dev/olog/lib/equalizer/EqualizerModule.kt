package dev.olog.lib.equalizer

import dagger.Binds
import dagger.Module
import dev.olog.lib.equalizer.bassboost.BassBoostImpl
import dev.olog.lib.equalizer.bassboost.BassBoostProxy
import dev.olog.lib.equalizer.bassboost.IBassBoost
import dev.olog.lib.equalizer.bassboost.IBassBoostInternal
import dev.olog.lib.equalizer.equalizer.EqualizerProxy
import dev.olog.lib.equalizer.equalizer.EqualizerWrapper
import dev.olog.lib.equalizer.equalizer.IEqualizer
import dev.olog.lib.equalizer.equalizer.IEqualizerInternal
import dev.olog.lib.equalizer.virtualizer.IVirtualizer
import dev.olog.lib.equalizer.virtualizer.IVirtualizerInternal
import dev.olog.lib.equalizer.virtualizer.VirtualizerImpl
import dev.olog.lib.equalizer.virtualizer.VirtualizerProxy
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