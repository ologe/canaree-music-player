package dev.olog.lib.di

import dagger.Binds
import dagger.Module
import dev.olog.domain.gateway.AlarmService
import dev.olog.lib.service.AlarmServiceImpl
import javax.inject.Singleton

@Module
abstract class ServiceModule {

    @Binds
    @Singleton
    internal abstract fun provideAlarmService(impl: AlarmServiceImpl): AlarmService

}