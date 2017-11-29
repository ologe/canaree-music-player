package dev.olog.presentation.dialog_add_playlist.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_add_playlist.AddPlaylistDialog

@Module(subcomponents = arrayOf(AddPlaylistDialogSubComponent::class))
abstract class AddPlaylistDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(AddPlaylistDialog::class)
    internal abstract fun injectorFactory(builder: AddPlaylistDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
