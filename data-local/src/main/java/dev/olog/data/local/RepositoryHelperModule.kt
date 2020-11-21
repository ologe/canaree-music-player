package dev.olog.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.data.local.migrations.Migration15to16
import dev.olog.data.local.migrations.Migration16to17
import dev.olog.data.local.migrations.Migration17to18
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal object RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(
                Migration15to16(),
                Migration16to17(),
                Migration17to18(),
            )
            .allowMainThreadQueries()
            .build()
    }
}