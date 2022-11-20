package dev.olog.image.provider.di

import com.bumptech.glide.load.engine.cache.DiskCache
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.image.provider.loader.MediaIdLoader
import okhttp3.OkHttpClient


@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ImageProviderComponent {

    fun mediaIdFactory(): MediaIdLoader.Factory
    fun okHttpClient(): OkHttpClient
    fun diskCache(): DiskCache

}

