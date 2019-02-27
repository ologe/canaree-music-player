package dev.olog.msc.presentation.dialog.clear.playlist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog

@Module(subcomponents = arrayOf(ClearPlaylistDialogSubComponent::class))
abstract class ClearPlaylistDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(ClearPlaylistDialog::class)
    internal abstract fun injectorFactory(builder: ClearPlaylistDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
