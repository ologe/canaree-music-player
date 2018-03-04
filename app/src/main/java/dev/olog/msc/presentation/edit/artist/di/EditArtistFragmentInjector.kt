package dev.olog.msc.presentation.edit.artist.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edit.artist.EditArtistFragment

@Module(subcomponents = arrayOf(EditArtistFragmentSubComponent::class))
abstract class EditArtistFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(EditArtistFragment::class)
    internal abstract fun injectorFactory(builder: EditArtistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
