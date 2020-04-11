package dev.olog.lib.audio.tagger.dagger

import dagger.Binds
import dagger.Module
import dev.olog.lib.audio.tagger.AudioTagger
import dev.olog.lib.audio.tagger.AudioTaggerImpl
import javax.inject.Singleton

@Module
abstract class AudioTaggerModule {

    @Binds
    @Singleton
    internal abstract fun provideAudioTagger(impl: AudioTaggerImpl): AudioTagger

}
