package dev.olog.msc.presentation.main.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.main.MainActivity

@Module(subcomponents = arrayOf(MainActivitySubComponent::class))
abstract class MainActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    internal abstract fun injectorFactory(builder: MainActivitySubComponent.Builder)
            : AndroidInjector.Factory<*>

}