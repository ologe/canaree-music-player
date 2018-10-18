package dev.olog.msc.presentation.dialog.delete.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.dialog.delete.DeleteDialog


@Module(subcomponents = arrayOf(DeleteDialogSubComponent::class))
abstract class DeleteDialogInjector {

    @Binds
    @IntoMap
    @FragmentXKey(DeleteDialog::class)
    internal abstract fun injectorFactory(builder: DeleteDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
