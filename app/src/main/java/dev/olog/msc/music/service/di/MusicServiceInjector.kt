package dev.olog.msc.music.service.di

import android.app.Service
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.music.service.MusicService


@Module(subcomponents = arrayOf(MusicServiceSubComponent::class))
abstract class MusicServiceInjector {

    @Binds
    @IntoMap
    @ClassKey(MusicService::class)
    internal abstract fun injectorFactory(builder: MusicServiceSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
