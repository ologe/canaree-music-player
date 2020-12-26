package dev.olog.msc.app

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.IEncrypter
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    companion object {

        @Provides
        @Reusable
        fun provideRandom(): Random = Random

    }

}