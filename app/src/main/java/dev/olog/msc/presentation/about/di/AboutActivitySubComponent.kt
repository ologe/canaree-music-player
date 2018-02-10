package dev.olog.msc.presentation.about.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.licenses.di.LicensesFragmentInjector
import dev.olog.msc.presentation.navigator.NavigatorModule
import dev.olog.msc.presentation.thanks.di.SpecialThanksFragmentInjector

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