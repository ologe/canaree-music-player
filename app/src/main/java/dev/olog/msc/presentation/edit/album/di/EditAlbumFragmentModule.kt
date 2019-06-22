package dev.olog.msc.presentation.edit.album.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.presentation.edit.album.EditAlbumFragment
import dev.olog.msc.presentation.edit.album.EditAlbumFragmentViewModel
import dev.olog.core.MediaId

@Module(includes = [EditAlbumFragmentModule.Binding::class])
class EditAlbumFragmentModule(private val fragment: EditAlbumFragment) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditAlbumFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(EditAlbumFragmentViewModel::class)
        fun provideViewModel(viewModel: EditAlbumFragmentViewModel): ViewModel

    }

}