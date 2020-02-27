package dev.olog.data.di

import android.app.AlarmManager
import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.AlarmService
import javax.inject.Singleton

@Module
abstract class ServiceModule {

    @Binds
    @Singleton
    internal abstract fun provideAlarmService(impl: AlarmManager): AlarmService

}