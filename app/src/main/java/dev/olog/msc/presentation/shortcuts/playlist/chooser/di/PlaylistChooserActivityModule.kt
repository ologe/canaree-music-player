package dev.olog.msc.presentation.shortcuts.playlist.chooser.di

import android.app.Activity
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity

@Module
class PlaylistChooserActivityModule(private val activity: PlaylistChooserActivity) {

    @Provides
    fun provideActivity(): Activity= activity

    @Provides
    @ActivityLifecycle
    fun provideLifecycle(): Lifecycle = activity.lifecycle

}