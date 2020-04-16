package dev.olog.lib.network.worker

import android.content.Context
import androidx.work.Configuration
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import javax.inject.Singleton

@Module
abstract class WorkersModule {

    @Binds
    internal abstract fun provideWorkerFactory(impl: AppWorkerFactory): WorkerFactory

    @Multibinds
    internal abstract fun workersMap(): Map<Class<out CoroutineWorker>, @JvmSuppressWildcards ChildWorkerFactory>

    companion object {

        @Provides
        @Singleton
        fun provideWorkManager(
            context: Context,factory:
            WorkerFactory
        ): WorkManager {
            val configuration = Configuration.Builder()
                .setWorkerFactory(factory)
                .build()

            WorkManager.initialize(context, configuration)
            return WorkManager.getInstance(context)
        }

    }

}