package dev.olog.msc.presentation.about.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.about.AboutActivity

@Module(subcomponents = arrayOf(AboutActivitySubComponent::class))
abstract class AboutActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(AboutActivity::class)
    internal abstract fun injectorFactory(builder: AboutActivitySubComponent.Builder)
            : AndroidInjector.Factory<*>

}
