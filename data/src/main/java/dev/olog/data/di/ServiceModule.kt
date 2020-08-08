package dev.olog.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.data.service.AlarmServiceImpl
import dev.olog.domain.gateway.AlarmService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    internal abstract fun provideAlarmService(impl: AlarmServiceImpl): AlarmService

}