package dev.olog.feature.settings.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.settings.SettingsFragment
import dev.olog.feature.settings.SettingsFragmentWrapper
import dev.olog.feature.settings.blacklist.BlacklistFragment
import dev.olog.feature.settings.categories.LibraryCategoriesFragment
import dev.olog.feature.settings.last.fm.LastFmCredentialsFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureSettingsDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideLibraryCategoriesFragment(): LibraryCategoriesFragment

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideBlacklistFragment(): BlacklistFragment

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideLastFmCredentialsFragment(): LastFmCredentialsFragment

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun providePreferencesFragment(): SettingsFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.SETTINGS)
            fun provideFragment(): Fragment {
                return SettingsFragmentWrapper()
            }

        }

    }

}