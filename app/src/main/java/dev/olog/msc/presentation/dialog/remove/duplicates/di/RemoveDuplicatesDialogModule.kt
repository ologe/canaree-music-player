package dev.olog.msc.presentation.dialog.remove.duplicates.di

import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.dialog.remove.duplicates.RemoveDuplicatesDialog
import dev.olog.core.MediaId

@Module
class RemoveDuplicatesDialogModule(private val fragment: RemoveDuplicatesDialog) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RemoveDuplicatesDialog.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideTitle(): String {
        return fragment.arguments!!.getString(RemoveDuplicatesDialog.ARGUMENTS_ITEM_TITLE)!!
    }

}