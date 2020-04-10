package dev.olog.feature.about.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.about.about.AboutFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureAboutDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [SettingsChildFragments::class])
        @FeatureScope
        internal abstract fun provideAboutFragment(): AboutFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.ABOUT)
            internal fun provideFragment(): Fragment {
                return AboutFragment()
            }

        }

    }

}