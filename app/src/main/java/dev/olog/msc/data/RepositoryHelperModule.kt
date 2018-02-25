package dev.olog.msc.data

import android.arch.persistence.room.Room
import android.content.Context
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
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

        return sqlBrite.wrapContentProvider(context.contentResolver, Schedulers.io())
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .fallbackToDestructiveMigration()
                .build()
    }

}