package dev.olog.msc.presentation.edit.album.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.edit.album.EditAlbumFragment
import dev.olog.msc.presentation.edit.album.EditAlbumFragmentViewModel
import dev.olog.msc.presentation.edit.album.EditAlbumFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

@Module
class EditAlbumFragmentModule(private val fragment: EditAlbumFragment) {

    @Provides
    fun provideViewModel(factory: EditAlbumFragmentViewModelFactory): EditAlbumFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(EditAlbumFragmentViewModel::class.java)
    }

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditAlbumFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

}