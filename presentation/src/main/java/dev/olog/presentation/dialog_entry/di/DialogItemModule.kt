package dev.olog.presentation.dialog_entry.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_entry.DialogItemFragment
import dev.olog.presentation.dialog_entry.DialogItemView
import dev.olog.presentation.dialog_entry.DialogItemViewModel
import dev.olog.presentation.dialog_entry.DialogItemViewModelFactory

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

    @Provides
    fun provideVIew(): DialogItemView = fragment

    @Provides
    fun provideViewModel(factory: DialogItemViewModelFactory) : DialogItemViewModel {
        return ViewModelProviders.of(fragment, factory).get(DialogItemViewModel::class.java)
    }

}