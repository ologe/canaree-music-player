package dev.olog.msc.presentation.dialog.delete.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.delete.DeleteDialog


@Module(subcomponents = arrayOf(DeleteDialogSubComponent::class))
abstract class DeleteDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(DeleteDialog::class)
    internal abstract fun injectorFactory(builder: DeleteDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
