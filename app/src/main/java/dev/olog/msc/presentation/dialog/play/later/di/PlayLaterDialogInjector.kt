package dev.olog.msc.presentation.dialog.play.later.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.dialog.play.later.PlayLaterDialog


@Module(subcomponents = arrayOf(PlayLaterDialogSubComponent::class))
abstract class PlayLaterDialogInjector {

    @Binds
    @IntoMap
    @FragmentXKey(PlayLaterDialog::class)
    internal abstract fun injectorFactory(builder: PlayLaterDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
