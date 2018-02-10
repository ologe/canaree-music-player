package dev.olog.msc.presentation.dialog.rename.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.dialog.rename.RenameDialog
import dev.olog.msc.utils.MediaId


@Module
class RenameDialogModule(
        private val fragment: RenameDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RenameDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

}