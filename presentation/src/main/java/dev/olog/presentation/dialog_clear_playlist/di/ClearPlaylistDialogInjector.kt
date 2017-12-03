package dev.olog.presentation.dialog_clear_playlist.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_clear_playlist.ClearPlaylistDialog

@Module(subcomponents = arrayOf(ClearPlaylistDialogSubComponent::class))
abstract class ClearPlaylistDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(ClearPlaylistDialog::class)
    internal abstract fun injectorFactory(builder: ClearPlaylistDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
