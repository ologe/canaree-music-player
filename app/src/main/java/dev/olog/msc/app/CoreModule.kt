package dev.olog.msc.app

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.IEncrypter

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class CoreModule {

    @Binds
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

}