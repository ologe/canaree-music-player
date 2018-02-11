package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import javax.inject.Singleton

@Module
class PreferenceModule{

    @Provides
    @Singleton
    fun provideAppPreferences(dataStore: AppPreferencesImpl): AppPreferencesGateway {
        return dataStore
    }

    @Provides
    @Singleton
    fun provideMusicPreferences(dataStore: MusicPreferencesImpl): MusicPreferencesGateway {
        return dataStore
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(preferences)
    }

    @Provides
    @Singleton
    fun provideEqualizerPreferences(dataStore: EqualizerPreferenceImpl): EqualizerPreferencesGateway {
        return dataStore
    }

}