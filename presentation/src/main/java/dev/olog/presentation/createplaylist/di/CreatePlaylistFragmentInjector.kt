package dev.olog.presentation.createplaylist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.createplaylist.CreatePlaylistFragment

@Module(subcomponents = arrayOf(CreatePlaylistFragmentSubComponent::class))
abstract class CreatePlaylistFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(CreatePlaylistFragment::class)
    internal abstract fun injectorFactory(builder: CreatePlaylistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
