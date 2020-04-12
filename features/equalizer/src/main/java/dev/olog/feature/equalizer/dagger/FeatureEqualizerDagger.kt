package dev.olog.feature.equalizer.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.equalizer.EqualizerFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureEqualizerDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [FragmentEqualizerModule::class])
        @FeatureScope
        internal abstract fun provideEqualizerFragment(): EqualizerFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.EQUALIZER)
            internal fun provideFragment(): Fragment {
                return EqualizerFragment()
            }

        }

    }

}