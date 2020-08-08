package dev.olog.lib.audio.tagger.dagger

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.lib.audio.tagger.AudioTagger
import dev.olog.lib.audio.tagger.AudioTaggerImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class AudioTaggerModule {

    @Binds
    @Singleton
    internal abstract fun provideAudioTagger(impl: AudioTaggerImpl): AudioTagger

}
