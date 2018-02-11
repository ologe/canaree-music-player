package dev.olog.msc.presentation.dialog.add.queue.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.add.queue.AddQueueDialog
import dev.olog.msc.utils.MediaId

@Module
class AddQueueDialogModule(
        private val fragment: AddQueueDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(AddQueueDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(AddQueueDialog.ARGUMENTS_LIST_SIZE)
    }

}