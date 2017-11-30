package dev.olog.presentation.dialog_rename.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_rename.RenameDialog


@Module
class RenameDialogModule(
        private val fragment: RenameDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(RenameDialog.ARGUMENTS_MEDIA_ID)
    }

}