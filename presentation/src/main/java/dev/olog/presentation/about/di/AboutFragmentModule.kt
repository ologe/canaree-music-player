package dev.olog.presentation.about.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.about.AboutFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl
import dev.olog.presentation.translations.TranslationsFragment

@Module
abstract class AboutFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun provideTranslationFragment(): TranslationsFragment

    @Binds
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

}