package dev.olog.msc.presentation.dialog.add.queue.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.add.queue.AddQueueDialog


@Module(subcomponents = arrayOf(AddQueueDialogSubComponent::class))
abstract class AddQueueDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(AddQueueDialog::class)
    internal abstract fun injectorFactory(builder: AddQueueDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
