package dev.olog.feature.queue.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.queue.PlayingQueueFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureQueueNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.QUEUE)
    fun provideLibraryTracksFragment(): Fragment = PlayingQueueFragment()

}