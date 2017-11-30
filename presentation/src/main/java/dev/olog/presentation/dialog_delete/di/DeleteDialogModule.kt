package dev.olog.presentation.dialog_delete.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_delete.DeleteDialog

@Module
class DeleteDialogModule(
        private val fragment: DeleteDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(DeleteDialog.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(DeleteDialog.ARGUMENTS_LIST_SIZE)
    }


}