package dev.olog.presentation.dialog_new_playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_new_playlist.NewPlaylistDialog
import dev.olog.shared.MediaId


@Module
class NewPlaylistDialogModule(
        private val fragment: NewPlaylistDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(NewPlaylistDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

}