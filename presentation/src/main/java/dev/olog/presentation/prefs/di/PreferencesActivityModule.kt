package dev.olog.presentation.prefs.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.prefs.PreferencesActivity
import dev.olog.presentation.pro.BillingImpl
import dev.olog.presentation.pro.IBilling

@Module
class PreferencesActivityModule(private val activity: PreferencesActivity) {

    @Provides
    internal fun provideActivity() : AppCompatActivity = activity

    @Provides
    @PerActivity
    internal fun provideBilling(impl: BillingImpl): IBilling = impl

}