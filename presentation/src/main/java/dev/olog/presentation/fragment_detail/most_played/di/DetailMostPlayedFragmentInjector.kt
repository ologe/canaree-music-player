package dev.olog.presentation.fragment_detail.most_played.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragment

@Module(subcomponents = arrayOf(DetailMostPlayedFragmentSubComponent::class))
abstract class DetailMostPlayedFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(DetailMostPlayedFragment::class)
    internal abstract fun injectorFactory(builder: DetailMostPlayedFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
