package dev.olog.presentation.dialog_clear_playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_clear_playlist.ClearPlaylistDialog
import javax.inject.Named

@Module
class ClearPlaylistDialogModule(
        private val fragment: ClearPlaylistDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(ClearPlaylistDialog.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    @Named("item title")
    fun provideItemTitle(): String {
        return fragment.arguments!!.getString(ClearPlaylistDialog.ARGUMENTS_ITEM_TITLE)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(ClearPlaylistDialog.ARGUMENTS_LIST_SIZE)
    }

}