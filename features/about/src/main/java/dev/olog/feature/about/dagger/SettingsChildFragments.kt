package dev.olog.feature.about.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.feature.about.translation.TranslationsFragment
import dev.olog.feature.presentation.base.dagger.ScreenScope

@Module
internal abstract class SettingsChildFragments {

    @ContributesAndroidInjector
    @ScreenScope
    internal abstract fun provideTranslationFragment(): TranslationsFragment

}