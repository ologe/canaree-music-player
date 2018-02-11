package dev.olog.msc.presentation.dialog.new.playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.new.playlist.NewPlaylistDialog
import dev.olog.msc.utils.MediaId


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