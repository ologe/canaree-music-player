package dev.olog.msc.presentation.edit.artist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.msc.presentation.edit.artist.EditArtistFragment
import dev.olog.msc.presentation.edit.artist.EditArtistFragmentViewModel
import dev.olog.core.MediaId

@Module(includes = [EditArtistFragmentModule.Binding::class])
class EditArtistFragmentModule(private val fragment: EditArtistFragment) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditArtistFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(EditArtistFragmentViewModel::class)
        fun provideViewModel(viewModel: EditArtistFragmentViewModel): ViewModel

    }

}