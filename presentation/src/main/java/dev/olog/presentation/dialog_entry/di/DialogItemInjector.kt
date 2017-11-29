package dev.olog.presentation.dialog_entry.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_entry.DialogItemFragment


@Module(subcomponents = arrayOf(DialogItemSubComponent::class))
abstract class DialogItemInjector {

    @Binds
    @IntoMap
    @FragmentKey(DialogItemFragment::class)
    internal abstract fun injectorFactory(builder: DialogItemSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
