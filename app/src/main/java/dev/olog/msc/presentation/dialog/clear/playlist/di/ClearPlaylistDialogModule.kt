package dev.olog.msc.presentation.dialog.clear.playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog
import dev.olog.msc.utils.MediaId

@Module
class ClearPlaylistDialogModule(
        private val fragment: ClearPlaylistDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(ClearPlaylistDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideTitle(): String {
        return fragment.arguments!!.getString(ClearPlaylistDialog.ARGUMENTS_ITEM_TITLE)
    }

}