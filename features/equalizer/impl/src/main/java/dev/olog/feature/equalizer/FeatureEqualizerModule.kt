package dev.olog.feature.equalizer

import android.os.Build
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.equalizer.impl.bass.BassBoostImpl
import dev.olog.feature.equalizer.impl.bass.BassBoostProxy
import dev.olog.feature.equalizer.impl.bass.IBassBoostInternal
import dev.olog.feature.equalizer.impl.equalizer.EqualizerImpl
import dev.olog.feature.equalizer.impl.equalizer.EqualizerImpl28
import dev.olog.feature.equalizer.impl.equalizer.EqualizerProxy
import dev.olog.feature.equalizer.impl.equalizer.IEqualizerInternal
import dev.olog.feature.equalizer.impl.virtualizer.IVirtualizerInternal
import dev.olog.feature.equalizer.impl.virtualizer.VirtualizerImpl
import dev.olog.feature.equalizer.impl.virtualizer.VirtualizerProxy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureEqualizerModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureEqualizerNavigatorImpl): FeatureEqualizerNavigator

    // proxies

    @Binds
    @Singleton
    abstract fun provideEqualizer(impl: EqualizerProxy): IEqualizer

    @Binds
    @Singleton
    abstract fun provideBassBoost(impl: BassBoostProxy): IBassBoost

    @Binds
    @Singleton
    abstract fun provideVirtualizer(impl: VirtualizerProxy): IVirtualizer



    // implementation

    @Binds
    abstract fun provideBassBoostInternal(impl: BassBoostImpl): IBassBoostInternal

    @Binds
    abstract fun provideVirtualizerInternal(impl: VirtualizerImpl): IVirtualizerInternal

    @Binds
    @Singleton
    internal abstract fun provideEqualizerPreferences(impl: EqualizerPreferenceImpl): EqualizerPreferencesGateway

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