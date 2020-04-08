package dev.olog.presentation.createplaylist.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.createplaylist.CreatePlaylistFragment
import dev.olog.feature.presentation.base.dagger.ScreenScope


@Subcomponent(modules = [CreatePlaylistFragmentModule::class])
@ScreenScope
interface CreatePlaylistFragmentSubComponent : AndroidInjector<CreatePlaylistFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<CreatePlaylistFragment> {

        override fun create(@BindsInstance instance: CreatePlaylistFragment): AndroidInjector<CreatePlaylistFragment>
    }

}