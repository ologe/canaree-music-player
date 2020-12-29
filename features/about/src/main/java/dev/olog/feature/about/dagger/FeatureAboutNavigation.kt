package dev.olog.feature.about.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.about.about.AboutFragment
import dev.olog.feature.about.license.LicensesFragment
import dev.olog.feature.about.localization.LocalizationFragment
import dev.olog.feature.about.special.thanks.SpecialThanksFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureAboutNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.ABOUT)
    fun provideAboutFragment(): Fragment = AboutFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LOCALIZATION)
    fun provideLocalizationFragment(): Fragment = LocalizationFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LICENSE)
    fun provideLicenseFragment(): Fragment = LicensesFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.SPECIAL_THANKS)
    fun provideSpecialThanksFragment(): Fragment = SpecialThanksFragment()

}