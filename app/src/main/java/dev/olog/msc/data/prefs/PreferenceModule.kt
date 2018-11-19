package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.prefs.app.AppPreferencesImpl
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import javax.inject.Singleton

@Module(includes = [PreferenceModule.Bindings::class])
class PreferenceModule{


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

    @Module
    interface Bindings {

        @Binds
        @Singleton
        fun provideEqualizerPreferences(dataStore: EqualizerPreferenceImpl): EqualizerPreferencesGateway

        @Binds
        @Singleton
        fun provideTutorialPreferences(dataStore: TutorialPreferenceImpl): TutorialPreferenceGateway

        @Binds
        @Singleton
        fun provideAppPreferences(dataStore: AppPreferencesImpl): AppPreferencesGateway

        @Binds
        @Singleton
        fun provideMusicPreferences(dataStore: MusicPreferencesImpl): MusicPreferencesGateway
    }

}