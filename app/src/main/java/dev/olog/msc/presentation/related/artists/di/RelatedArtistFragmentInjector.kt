package dev.olog.msc.presentation.related.artists.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment

@Module(subcomponents = arrayOf(RelatedArtistFragmentSubComponent::class))
abstract class RelatedArtistFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(RelatedArtistFragment::class)
    internal abstract fun injectorFactory(builder: RelatedArtistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
