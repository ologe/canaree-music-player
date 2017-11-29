package dev.olog.presentation.dialog_new_playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_new_playlist.NewPlaylistDialog


@Module
class NewPlaylistDialogModule(
        private val fragment: NewPlaylistDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(NewPlaylistDialog.ARGUMENTS_MEDIA_ID)
    }

}