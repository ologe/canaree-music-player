package dev.olog.presentation.dialog_delete.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_delete.DeleteDialog
import dev.olog.shared.MediaId

@Module
class DeleteDialogModule(
        private val fragment: DeleteDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DeleteDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(DeleteDialog.ARGUMENTS_LIST_SIZE)
    }

    @Provides
    fun provideItemTitle(): String {
        return fragment.arguments!!.getString(DeleteDialog.ARGUMENTS_ITEM_TITLE)
    }
}