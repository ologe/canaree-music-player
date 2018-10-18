package dev.olog.msc.presentation.edit.artist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.edit.artist.EditArtistFragment

@Module(subcomponents = arrayOf(EditArtistFragmentSubComponent::class))
abstract class EditArtistFragmentInjector {

    @Binds
    @IntoMap
    @FragmentXKey(EditArtistFragment::class)
    internal abstract fun injectorFactory(builder: EditArtistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
