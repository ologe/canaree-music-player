package dev.olog.msc.presentation.preferences.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.interfaces.pro.BillingImpl
import dev.olog.msc.interfaces.pro.IBilling
import dev.olog.msc.presentation.preferences.PreferencesActivity

@Module
class PreferencesActivityModule(private val activity: PreferencesActivity) {

    @Provides
    internal fun provideActivity() : AppCompatActivity = activity

    @Provides
    internal fun provideBilling(impl: BillingImpl): IBilling = impl

}