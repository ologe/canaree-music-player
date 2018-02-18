package dev.olog.msc.presentation.edit.info.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edit.info.EditSongFragment

@Module(subcomponents = arrayOf(EditSongFragmentSubComponent::class))
abstract class EditSongFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(EditSongFragment::class)
    internal abstract fun injectorFactory(builder: EditSongFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
