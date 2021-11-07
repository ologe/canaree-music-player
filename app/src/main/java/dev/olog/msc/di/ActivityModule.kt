package dev.olog.msc.di

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.media.MediaProvider

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    companion object {
        @Provides
        internal fun provideMusicGlue(instance: FragmentActivity): MediaProvider = instance as MediaProvider
    }

}