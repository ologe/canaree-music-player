package dev.olog.data.remote

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class DataRemoteModule {

    @Binds
    @Singleton
    abstract fun provideTrack(impl: ImageRetrieverRemoteTrackImpl): ImageRetrieverRemoteTrack

    @Binds
    @Singleton
    abstract fun provideAlbum(impl: ImageRetrieverRemoteAlbumImpl): ImageRetrieverRemoteAlbum

    @Binds
    @Singleton
    abstract fun provideArtist(impl: ImageRetrieverRemoteArtistImpl): ImageRetrieverRemoteArtist

}