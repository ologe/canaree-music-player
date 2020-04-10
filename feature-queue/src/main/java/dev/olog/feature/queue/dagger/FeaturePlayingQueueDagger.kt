package dev.olog.feature.queue.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.queue.PlayingQueueFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeaturePlayingQueueDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [PlayingQueueModule::class])
        @FeatureScope
        internal abstract fun provideQueue(): PlayingQueueFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.QUEUE)
            internal fun provideFragment(): Fragment {
                return PlayingQueueFragment.newInstance()
            }

        }

    }

}