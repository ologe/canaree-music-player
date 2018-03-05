package dev.olog.msc.presentation.edit.artist.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.edit.artist.EditArtistFragment
import dev.olog.msc.presentation.edit.artist.EditArtistFragmentViewModel
import dev.olog.msc.presentation.edit.artist.EditArtistFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

@Module
class EditArtistFragmentModule(private val fragment: EditArtistFragment) {

    @Provides
    fun provideViewModel(factory: EditArtistFragmentViewModelFactory): EditArtistFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(EditArtistFragmentViewModel::class.java)
    }

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditArtistFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }


}