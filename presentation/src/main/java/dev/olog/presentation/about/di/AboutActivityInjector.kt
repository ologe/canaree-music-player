package dev.olog.presentation.about.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.about.AboutActivity

@Module(subcomponents = arrayOf(AboutActivitySubComponent::class))
abstract class AboutActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(AboutActivity::class)
    internal abstract fun injectorFactory(builder: AboutActivitySubComponent.Builder)
            : AndroidInjector.Factory<*>

}
