package dev.olog.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Module
import dagger.Provides
import dev.olog.domain.gateway.AppPreferencesGateway
import dev.olog.shared.ApplicationContext
import javax.inject.Singleton

@Module
class PreferenceModule{

    @Provides
    @Singleton
    fun provideAppPreferences(dataStore: AppPreferenceImpl): AppPreferencesGateway {
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

}