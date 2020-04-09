package dev.olog.feature.service.floating.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.service.floating.FloatingWindowService

class FeatureFloatingWindowDagger {

    @Subcomponent(modules = [FloatingWindowServiceModule::class])
    @FeatureScope
    internal interface Graph : AndroidInjector<FloatingWindowService> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<FloatingWindowService>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(FloatingWindowService::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

    }

}