package dev.olog.feature.service.music.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.service.music.MusicService
import dev.olog.feature.service.music.notification.NotificationModule

class FeatureMusicServiceDagger {

    @Subcomponent(
        modules = [
            MusicServiceModule::class,
            NotificationModule::class
        ]
    )
    @FeatureScope
    internal interface Graph : AndroidInjector<MusicService> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<MusicService>
    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(MusicService::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

    }

}