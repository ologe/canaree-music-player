package dev.olog.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.Resettable
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.data.prefs.AppPreferencesImpl
import dev.olog.data.prefs.BlacklistPreferenceImpl
import dev.olog.data.prefs.TutorialPreferenceImpl
import dev.olog.data.prefs.sort.AppSortingImpl
import dev.olog.feature.equalizer.EqualizerPreferencesGateway
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @IntoSet
    internal abstract fun provideEqualizerPreferencesResettable(impl: EqualizerPreferencesGateway): Resettable

    @Binds
    @Singleton
    internal abstract fun provideTutorialPreferences(impl: TutorialPreferenceImpl): TutorialPreferenceGateway

    @Binds
    @Singleton
    internal abstract fun provideAppPreferences(impl: AppPreferencesImpl): AppPreferencesGateway

    @Binds
    @IntoSet
    internal abstract fun provideAppPreferencesResettable(impl: AppPreferencesGateway): Resettable

    @Binds
    @Singleton
    internal abstract fun provideSortPreferences(impl: AppSortingImpl): SortPreferences

    @Binds
    @Singleton
    internal abstract fun provideBlacklistPreferences(impl: BlacklistPreferenceImpl): BlacklistPreferences

    @Binds
    @IntoSet
    abstract fun provideResettable(impl: BlacklistPreferences): Resettable

    companion object {
        @Provides
        @Singleton
        internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

}