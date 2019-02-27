package dev.olog.msc.presentation.dialog.play.later.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.play.later.PlayLaterDialog


@Module(subcomponents = arrayOf(PlayLaterDialogSubComponent::class))
abstract class PlayLaterDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(PlayLaterDialog::class)
    internal abstract fun injectorFactory(builder: PlayLaterDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
