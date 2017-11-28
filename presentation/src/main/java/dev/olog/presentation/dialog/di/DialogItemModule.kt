package dev.olog.presentation.dialog.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog.DialogItemFragment

@Module
class DialogItemModule(
        private val fragment: DialogItemFragment
) {

    @Provides
    fun provideInstance() : DialogItemFragment = fragment

    @Provides
    fun provideMediaId(): String{
        return fragment.arguments!!.getString(DialogItemFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    fun providePosition(): Int {
        return fragment.arguments!!.getInt(DialogItemFragment.ARGUMENTS_LIST_POSITION)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}