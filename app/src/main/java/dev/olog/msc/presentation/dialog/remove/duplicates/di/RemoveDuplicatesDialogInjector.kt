package dev.olog.msc.presentation.dialog.remove.duplicates.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.remove.duplicates.RemoveDuplicatesDialog

@Module(subcomponents = arrayOf(RemoveDuplicatesDialogSubComponent::class))
abstract class RemoveDuplicatesDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(RemoveDuplicatesDialog::class)
    internal abstract fun injectorFactory(builder: RemoveDuplicatesDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
