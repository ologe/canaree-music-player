package dev.olog.presentation.fragment_mini_player.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment

@Module(subcomponents = arrayOf(MiniPlayerFragmentSubComponent::class))
abstract class MiniPlayerFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(MiniPlayerFragment::class)
    internal abstract fun injectorFactory(builder: MiniPlayerFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>


}
