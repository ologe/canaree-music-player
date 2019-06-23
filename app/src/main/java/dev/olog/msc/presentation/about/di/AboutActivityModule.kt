package dev.olog.msc.presentation.about.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.PerActivity
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.presentation.navigator.NavigatorAboutImpl
import dev.olog.msc.pro.BillingImpl
import dev.olog.msc.pro.IBilling

@Module(includes = [AboutActivityModule.Bindings::class])
class AboutActivityModule(
        private val activity: AboutActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Module
    interface Bindings {

        @Binds
        fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

        @Binds
        @PerActivity
        fun provideBilling(impl: BillingImpl): IBilling

    }

}