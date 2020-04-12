package dev.olog.feature.onboarding.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.feature.onboarding.SplashFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureOnboardingDagger {

    @Module
    class AppModule {

        @Provides
        @IntoMap
        @FragmentScreenKey(FragmentScreen.ONBOARDING)
        fun provideFragment(): Fragment {
            return SplashFragment()
        }

    }

}