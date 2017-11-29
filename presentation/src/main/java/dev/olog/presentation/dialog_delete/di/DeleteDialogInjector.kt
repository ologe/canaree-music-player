package dev.olog.presentation.dialog_delete.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_delete.DeleteDialog


@Module(subcomponents = arrayOf(DeleteDialogSubComponent::class))
abstract class DeleteDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(DeleteDialog::class)
    internal abstract fun injectorFactory(builder: DeleteDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
