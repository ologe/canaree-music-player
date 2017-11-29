package dev.olog.presentation.dialog_share.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_share.ShareDialog

@Module
class ShareDialogModule(
        private val fragment: ShareDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(ShareDialog.ARGUMENTS_MEDIA_ID)
    }

}