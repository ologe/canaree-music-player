package dev.olog.presentation.prefs.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.prefs.SettingsFragment
import dev.olog.presentation.prefs.blacklist.BlacklistFragment
import dev.olog.presentation.prefs.categories.LibraryCategoriesFragment
import dev.olog.presentation.prefs.lastfm.LastFmCredentialsFragment

@Module
abstract class SettingsFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideLibraryCategoriesFragment() : LibraryCategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideBlacklistFragment() : BlacklistFragment

    @ContributesAndroidInjector
    abstract fun provideLastFmCredentialsFragment() : LastFmCredentialsFragment

    @ContributesAndroidInjector
    abstract fun providePreferencesFragment() : SettingsFragment

}