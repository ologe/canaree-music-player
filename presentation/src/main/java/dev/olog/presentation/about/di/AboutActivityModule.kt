package dev.olog.presentation.about.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dev.olog.presentation.about.AboutActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl
import dev.olog.presentation.pro.BillingImpl
import dev.olog.presentation.pro.IBilling

@Module
abstract class AboutActivityModule {

    @Binds
    abstract fun provideActivity(instance: AboutActivity): AppCompatActivity

    @Binds
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

    @Binds
    @PerActivity
    abstract fun provideBilling(impl: BillingImpl): IBilling

}