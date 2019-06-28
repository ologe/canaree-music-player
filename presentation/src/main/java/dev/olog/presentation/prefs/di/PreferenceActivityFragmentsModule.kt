package dev.olog.presentation.prefs.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.prefs.PreferencesFragment
import dev.olog.presentation.prefs.blacklist.BlacklistFragment
import dev.olog.presentation.prefs.categories.LibraryCategoriesFragment
import dev.olog.presentation.prefs.lastfm.LastFmCredentialsFragment

@Module
abstract class PreferenceActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideLibraryCategoriesFragment() : LibraryCategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideBlacklistFragment() : BlacklistFragment

    @ContributesAndroidInjector
    abstract fun provideLastFmCredentialsFragment() : LastFmCredentialsFragment

    @ContributesAndroidInjector
    abstract fun providePreferencesFragment() : PreferencesFragment

}