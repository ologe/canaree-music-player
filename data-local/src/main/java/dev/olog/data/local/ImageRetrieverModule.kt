package dev.olog.data.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.data.local.last.fm.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class ImageRetrieverModule {

    @Binds
    @Singleton
    abstract fun provideTrack(impl: ImageRetrieverLocalTrackImpl): ImageRetrieverLocalTrack

    @Binds
    @Singleton
    abstract fun provideAlbum(impl: ImageRetrieverLocalAlbumImpl): ImageRetrieverLocalAlbum

    @Binds
    @Singleton
    abstract fun provideArtist(impl: ImageRetrieverLocalArtistImpl): ImageRetrieverLocalArtist

}