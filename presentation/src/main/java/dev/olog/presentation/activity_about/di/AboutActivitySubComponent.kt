package dev.olog.presentation.activity_about.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_about.AboutActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.fragment_licenses.di.LicensesFragmentInjector
import dev.olog.presentation.fragment_special_thanks.di.SpecialThanksFragmentInjector
import dev.olog.presentation.navigation.NavigatorModule

@Subcomponent(modules = arrayOf(
        AboutActivityModule::class,
        NavigatorModule::class,

        SpecialThanksFragmentInjector::class,
        LicensesFragmentInjector::class
))
@PerActivity
interface AboutActivitySubComponent : AndroidInjector<AboutActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AboutActivity>() {

        abstract fun module(module: AboutActivityModule): Builder

        override fun seedInstance(instance: AboutActivity) {
            module(AboutActivityModule(instance))
        }
    }

}