package dev.olog.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.*
import dev.olog.data.prefs.*
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
    internal abstract fun provideBlacklistPreferences(impl: BlacklistPreferenceImpl): BlacklistPreferences

    @Module
    companion object {
        @Provides
        @JvmStatic
        @Singleton
        internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @Provides
        @JvmStatic
        @Singleton
        internal fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
            return RxSharedPreferences.create(preferences)
        }
    }

}