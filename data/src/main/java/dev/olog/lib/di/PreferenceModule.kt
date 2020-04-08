package dev.olog.lib.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.domain.prefs.*
import dev.olog.lib.prefs.*
import dev.olog.lib.prefs.sort.AppSortingImpl
import javax.inject.Singleton

@Module
abstract class PreferenceModule {

    @Binds
    @Singleton
    internal abstract fun provideEqualizerPreferences(impl: EqualizerPreferenceImpl): EqualizerPreferencesGateway

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

    @Binds
    @Singleton
    internal abstract fun provideBlacklistPreferences(impl: BlacklistPreferencesImpl): BlacklistPreferences

    companion object {

        @Provides
        @Singleton
        internal fun provideSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

}