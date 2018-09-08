package dev.olog.msc.presentation.playlist.track.chooser.di

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragmentViewModel

@Module(includes = [PlaylistTracksChooserFragmentModule.Binding::class])
class PlaylistTracksChooserFragmentModule(private val fragment: PlaylistTracksChooserFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(PlaylistTracksChooserFragmentViewModel::class)
        fun provideViewModel(viewModel: PlaylistTracksChooserFragmentViewModel): ViewModel

    }

}