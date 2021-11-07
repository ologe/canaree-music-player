package dev.olog.data.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.data.prefs.AppPreferencesImpl
import dev.olog.data.prefs.MusicPreferencesImpl
import dev.olog.data.prefs.TutorialPreferenceImpl
import dev.olog.data.prefs.sort.AppSortingImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @Singleton
    internal abstract fun provideTutorialPreferences(impl: TutorialPreferenceImpl): TutorialPreferenceGateway

    @Binds
    @Singleton
    internal abstract fun provideAppPreferences(impl: AppPreferencesImpl): AppPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideMusicPreferences(impl: MusicPreferencesImpl): MusicPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideSortPreferences(impl: AppSortingImpl): SortPreferences

    companion object {
        @Provides
        @Singleton
        internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

}