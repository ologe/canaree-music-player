package dev.olog.feature.detail.dagger

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.detail.DetailFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureDetailDagger {

    @Subcomponent(modules = [DetailFragmentModule::class])
    @FeatureScope
    internal interface Graph : AndroidInjector<DetailFragment> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<DetailFragment>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(DetailFragment::class)
        internal abstract fun injectorFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.DETAIL)
            internal fun provideFragment(): Fragment {
                return DetailFragment()
            }

        }

    }

}