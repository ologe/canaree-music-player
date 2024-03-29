package dev.olog.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.data.db.AppDatabase
import dev.olog.data.db.entities.CustomTypeConverters
import dev.olog.data.db.migration.Migration15To16
import dev.olog.data.db.migration.Migration16To17
import dev.olog.data.db.migration.Migration17To18
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(
        @ApplicationContext context: Context,
        converter: CustomTypeConverters,
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(
                Migration15To16(),
                Migration16To17(),
                Migration17To18(),
            )
            .addTypeConverter(converter)
            .allowMainThreadQueries()
            .build()
    }

}