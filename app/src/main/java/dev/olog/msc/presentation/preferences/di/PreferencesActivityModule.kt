package dev.olog.msc.presentation.preferences.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.PerActivity
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.pro.BillingImpl
import dev.olog.msc.pro.IBilling

@Module
class PreferencesActivityModule(private val activity: PreferencesActivity) {

    @Provides
    internal fun provideActivity() : AppCompatActivity = activity

    @Provides
    @PerActivity
    internal fun provideBilling(impl: BillingImpl): IBilling = impl

}