package dev.olog.presentation.dialog_rename.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_rename.RenameDialog


@Module(subcomponents = arrayOf(RenameDialogSubComponent::class))
abstract class RenameDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(RenameDialog::class)
    internal abstract fun injectorFactory(builder: RenameDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
