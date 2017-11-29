package dev.olog.presentation.dialog_add_playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_add_playlist.AddPlaylistDialog

@Module
class AddPlaylistDialogModule(
        private val fragment: AddPlaylistDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(AddPlaylistDialog.ARGUMENTS_MEDIA_ID)
    }

}