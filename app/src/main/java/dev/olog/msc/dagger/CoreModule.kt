package dev.olog.msc.dagger

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.domain.IEncrypter
import dev.olog.msc.EncrypterImpl

@Module
@InstallIn(ApplicationComponent::class)
abstract class CoreModule {

    @Binds
    internal abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

}