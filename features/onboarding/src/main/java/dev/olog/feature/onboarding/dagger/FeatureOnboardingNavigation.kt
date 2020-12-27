package dev.olog.feature.onboarding.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.onboarding.SplashFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureOnboardingDagger {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.ONBOARDING)
    fun provideFragment(): Fragment = SplashFragment()

}