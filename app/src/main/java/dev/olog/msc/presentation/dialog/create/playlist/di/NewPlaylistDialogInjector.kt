package dev.olog.msc.presentation.dialog.create.playlist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog

@Module(subcomponents = arrayOf(NewPlaylistDialogSubComponent::class))
abstract class NewPlaylistDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(NewPlaylistDialog::class)
    internal abstract fun injectorFactory(builder: NewPlaylistDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
