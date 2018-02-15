package dev.olog.msc.presentation.dialog.create.playlist.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog
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

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(NewPlaylistDialog.ARGUMENTS_LIST_SIZE)
    }

    @Provides
    fun provideTitle(): String = fragment.arguments!!.getString(NewPlaylistDialog.ARGUMENTS_ITEM_TITLE)

}