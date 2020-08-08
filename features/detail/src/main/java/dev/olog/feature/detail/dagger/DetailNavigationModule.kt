package dev.olog.feature.detail.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.detail.DetailFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object DetailNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DETAIL)
    internal fun provideFragment(): Fragment {
        return DetailFragment()
    }

}