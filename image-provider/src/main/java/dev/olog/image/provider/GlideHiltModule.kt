package dev.olog.image.provider

import android.view.View
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

@Module
@InstallIn(ViewComponent::class)
class GlideHiltModule {

    @Provides
    fun provideRequestManager(view: View): RequestManager {
        return GlideApp.with(view)
    }

}