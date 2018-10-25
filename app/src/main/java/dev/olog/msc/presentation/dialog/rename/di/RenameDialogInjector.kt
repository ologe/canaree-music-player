package dev.olog.msc.presentation.dialog.rename.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.rename.RenameDialog


@Module(subcomponents = arrayOf(RenameDialogSubComponent::class))
abstract class RenameDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(RenameDialog::class)
    internal abstract fun injectorFactory(builder: RenameDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
