package dev.olog.msc.presentation.player.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.player.PlayerFragment


@Module(subcomponents = arrayOf(PlayerFragmentSubComponent::class))
abstract class PlayerFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(PlayerFragment::class)
    internal abstract fun injectorFactory(builder: PlayerFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
