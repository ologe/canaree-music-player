package dev.olog.image.provider.di

import android.content.Context
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ImageProviderModule {

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context,
    ): DiskCache {
        return InternalCacheDiskCacheFactory(context).build()!!
    }

}