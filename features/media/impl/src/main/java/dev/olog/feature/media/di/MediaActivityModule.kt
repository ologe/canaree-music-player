package dev.olog.feature.media.di

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.feature.media.api.MediaProvider
import dev.olog.shared.extension.findInContext

@Module
@InstallIn(ActivityComponent::class)
abstract class MediaActivityModule {

    companion object {
        @Provides
        internal fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
            return instance.findInContext()
        }
    }

}