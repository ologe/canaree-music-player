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
import dagger.multibindings.IntoSet
import dev.olog.core.ResettablePreference
import dev.olog.core.prefs.*
import dev.olog.data.local.prefs.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class PreferenceModule {


    @Binds
    @Singleton
    internal abstract fun provideEqualizerPreferences(impl: EqualizerPreferenceImpl): EqualizerPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideTutorialPreferences(impl: TutorialPreferenceGatewayImpl): TutorialPreferenceGateway

    @Binds
    @Singleton
    internal abstract fun provideAppPreferences(impl: AppPreferencesGatewayImpl): AppPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideMusicPreferences(impl: MusicPreferencesGatewayImpl): MusicPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideSortPreferences(impl: SortPreferencesGatewayImpl): SortPreferencesGateway

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

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class ResettablePreferenceModule {

    @Binds
    @IntoSet
    internal abstract fun provideResettableEqualizer(impl: EqualizerPreferenceImpl): ResettablePreference

    @Binds
    @IntoSet
    internal abstract fun provideResettableTutorial(impl: TutorialPreferenceGatewayImpl): ResettablePreference

    @Binds
    @IntoSet
    internal abstract fun provideResettableAppPrefs(impl: AppPreferencesGatewayImpl): ResettablePreference

    @Binds
    @IntoSet
    internal abstract fun provideResettableMusicPrefs(impl: MusicPreferencesGatewayImpl): ResettablePreference

    @Binds
    @IntoSet
    internal abstract fun provideResettableSort(impl: SortPreferencesGatewayImpl): ResettablePreference

    @Binds
    @IntoSet
    internal abstract fun provideResettableBlacklist(impl: BlacklistPreferenceImpl): ResettablePreference

}