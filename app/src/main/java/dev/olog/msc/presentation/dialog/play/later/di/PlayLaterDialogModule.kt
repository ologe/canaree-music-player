package dev.olog.msc.presentation.dialog.play.later.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.play.later.PlayLaterDialog
import dev.olog.msc.utils.MediaId

@Module
class PlayLaterDialogModule(
        private val fragment: PlayLaterDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(PlayLaterDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(PlayLaterDialog.ARGUMENTS_LIST_SIZE)
    }

    @Provides
    fun provideTitle(): String = fragment.arguments!!.getString(PlayLaterDialog.ARGUMENTS_ITEM_TITLE)

}