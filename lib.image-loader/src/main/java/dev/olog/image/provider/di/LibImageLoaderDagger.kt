package dev.olog.image.provider.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.image.provider.GlideModule

class LibImageLoaderDagger {

    @Subcomponent
    @FeatureScope
    internal interface Graph : AndroidInjector<GlideModule> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<GlideModule>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(GlideModule::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

    }

}