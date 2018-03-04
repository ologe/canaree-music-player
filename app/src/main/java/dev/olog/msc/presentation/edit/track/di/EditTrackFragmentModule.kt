package dev.olog.msc.presentation.edit.track.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.edit.track.EditTrackFragment
import dev.olog.msc.presentation.edit.track.EditTrackFragmentViewModel
import dev.olog.msc.presentation.edit.track.EditTrackFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

@Module
class EditTrackFragmentModule(
        private val fragment: EditTrackFragment

) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditTrackFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: EditTrackFragmentViewModelFactory): EditTrackFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(EditTrackFragmentViewModel::class.java)
    }

}