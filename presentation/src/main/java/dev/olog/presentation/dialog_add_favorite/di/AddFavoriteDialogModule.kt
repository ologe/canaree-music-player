package dev.olog.presentation.dialog_add_favorite.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_add_favorite.AddFavoriteDialog
import dev.olog.shared.MediaId

@Module
class AddFavoriteDialogModule(
        private val fragment: AddFavoriteDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(AddFavoriteDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(AddFavoriteDialog.ARGUMENTS_LIST_SIZE)
    }

}