package dev.olog.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.prefs.*
import dev.olog.data.local.prefs.*
import dev.olog.data.local.prefs.AppSortingImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class PreferenceModule {

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
    internal abstract fun provideBlacklistPreferences(impl: BlacklistPreferenceImpl): BlacklistPreferences

    companion object {
        @Provides
        @Singleton
        internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

}