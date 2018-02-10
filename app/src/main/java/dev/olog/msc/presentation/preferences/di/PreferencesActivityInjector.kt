package dev.olog.msc.presentation.preferences.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.preferences.PreferencesActivity

@Module(subcomponents = arrayOf(PreferencesActivitySubComponent::class))
abstract class PreferencesActivityInjector {

    @Binds
    @IntoMap
    @ActivityKey(PreferencesActivity::class)
    internal abstract fun injectorFactory(builder: PreferencesActivitySubComponent.Builder)
            : AndroidInjector.Factory<out Activity>

}