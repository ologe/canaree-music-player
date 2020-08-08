package dev.olog.feature.equalizer.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.equalizer.EqualizerFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object EqualizerNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.EQUALIZER)
    internal fun provideFragment(): Fragment {
        return EqualizerFragment()
    }

}