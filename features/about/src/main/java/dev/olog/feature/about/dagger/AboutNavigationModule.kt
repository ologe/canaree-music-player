package dev.olog.feature.about.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.about.about.AboutFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object AboutNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.ABOUT)
    internal fun provideFragment(): Fragment {
        return AboutFragment()
    }

}