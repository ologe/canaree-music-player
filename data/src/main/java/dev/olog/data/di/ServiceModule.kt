package dev.olog.data.di

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.AlarmService
import dev.olog.data.service.AlarmServiceImpl
import javax.inject.Singleton

@Module
abstract class ServiceModule {

    @Binds
    @Singleton
    internal abstract fun provideAlarmService(impl: AlarmServiceImpl): AlarmService

}