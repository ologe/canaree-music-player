package dev.olog.msc.app

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.media.MediaProvider

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    internal fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
        require(instance is MediaProvider)
        return instance
    }

}