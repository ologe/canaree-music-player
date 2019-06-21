package dev.olog.msc.data

import android.content.Context
import androidx.room.Room
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dagger.Module
import dagger.Provides
import dev.olog.msc.BuildConfig
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class RepositoryHelperModule {

    @Provides
    @Singleton
    fun provideSqlBrite(): SqlBrite = SqlBrite.Builder().build()

    @Provides
    @Singleton
    fun provideBriteContentResolver(@ApplicationContext context: Context,
                                    sqlBrite: SqlBrite) : BriteContentResolver {

        val contentProvider = sqlBrite.wrapContentProvider(context.contentResolver, Schedulers.io())
        contentProvider.setLoggingEnabled(BuildConfig.DEBUG)
        return contentProvider
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // 1 to 2
                .build()
    }

}