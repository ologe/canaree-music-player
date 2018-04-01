package dev.olog.msc.presentation.playlist.track.chooser.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragmentViewModel
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragmentViewModelFactory

@Module
class PlaylistTracksChooserFragmentModule(private val fragment: PlaylistTracksChooserFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: PlaylistTracksChooserFragmentViewModelFactory): PlaylistTracksChooserFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(PlaylistTracksChooserFragmentViewModel::class.java)
    }

}