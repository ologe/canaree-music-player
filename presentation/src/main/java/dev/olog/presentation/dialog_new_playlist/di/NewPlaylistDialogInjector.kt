package dev.olog.presentation.dialog_new_playlist.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_new_playlist.NewPlaylistDialog

@Module(subcomponents = arrayOf(NewPlaylistDialogSubComponent::class))
abstract class NewPlaylistDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(NewPlaylistDialog::class)
    internal abstract fun injectorFactory(builder: NewPlaylistDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
