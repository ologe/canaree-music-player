package dev.olog.presentation.dialog_add_queue.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_add_queue.AddQueueDialog

@Module
class AddQueueDialogModule(
        private val fragment: AddQueueDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(AddQueueDialog.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(AddQueueDialog.ARGUMENTS_LIST_SIZE)
    }

}