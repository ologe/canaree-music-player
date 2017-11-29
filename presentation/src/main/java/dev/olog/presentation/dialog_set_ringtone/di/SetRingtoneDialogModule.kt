package dev.olog.presentation.dialog_set_ringtone.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_set_ringtone.SetRingtoneDialog

@Module
class SetRingtoneDialogModule(
        private val fragment: SetRingtoneDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(SetRingtoneDialog.ARGUMENTS_MEDIA_ID)
    }

}