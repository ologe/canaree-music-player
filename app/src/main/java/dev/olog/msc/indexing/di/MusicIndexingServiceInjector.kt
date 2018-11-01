package dev.olog.msc.indexing.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.indexing.MusicIndexingUpdateService

@Module
abstract class MusicIndexingServiceInjector {

    @ContributesAndroidInjector
    abstract fun provideMusicIndexingService(): MusicIndexingUpdateService


}