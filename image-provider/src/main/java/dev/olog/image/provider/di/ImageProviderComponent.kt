package dev.olog.image.provider.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.image.provider.loader.GlideImageRetrieverLoader
import dev.olog.image.provider.loader.GlideMergedImageLoader
import dev.olog.image.provider.loader.GlideOriginalImageLoader


@EntryPoint
@InstallIn(SingletonComponent::class)
interface ImageProviderComponent {

    fun lastFmFactory(): GlideImageRetrieverLoader.Factory
    fun originalFactory(): GlideOriginalImageLoader.Factory
    fun mergedFactory(): GlideMergedImageLoader.Factory
    fun config(): Config

}