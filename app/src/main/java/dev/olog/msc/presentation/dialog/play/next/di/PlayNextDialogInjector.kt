package dev.olog.msc.presentation.dialog.play.next.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.play.next.PlayNextDialog


@Module(subcomponents = arrayOf(PlayNextDialogSubComponent::class))
abstract class PlayNextDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(PlayNextDialog::class)
    internal abstract fun injectorFactory(builder: PlayNextDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
