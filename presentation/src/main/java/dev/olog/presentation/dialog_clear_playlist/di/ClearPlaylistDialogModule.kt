package dev.olog.presentation.dialog_clear_playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_clear_playlist.ClearPlaylistDialog
import dev.olog.shared.MediaId

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
    fun provideItemTitle(): String {
        return fragment.arguments!!.getString(ClearPlaylistDialog.ARGUMENTS_ITEM_TITLE)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(ClearPlaylistDialog.ARGUMENTS_LIST_SIZE)
    }

}