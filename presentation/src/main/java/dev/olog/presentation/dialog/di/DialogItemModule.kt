package dev.olog.presentation.dialog.di

import dagger.Module
import dagger.Provides
import dev.olog.presentation.dialog.DialogItemFragment

@Module
class DialogItemModule(
        private val fragment: DialogItemFragment
) {

    @Provides
    fun provideMediaId(): String{
        return fragment.arguments!!.getString(DialogItemFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    fun providePosition(): Int {
        return fragment.arguments!!.getInt(DialogItemFragment.ARGUMENTS_LIST_POSITION)
    }


}