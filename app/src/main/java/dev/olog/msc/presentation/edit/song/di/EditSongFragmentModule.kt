package dev.olog.msc.presentation.edit.song.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.edit.song.EditSongFragment
import dev.olog.msc.presentation.edit.song.EditSongFragmentViewModel
import dev.olog.msc.presentation.edit.song.EditSongFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

@Module
class EditSongFragmentModule(
        private val fragment: EditSongFragment

) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditSongFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: EditSongFragmentViewModelFactory): EditSongFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(EditSongFragmentViewModel::class.java)
    }

}