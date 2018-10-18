package dev.olog.msc.presentation.dialog.set.ringtone.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog

@Module(subcomponents = arrayOf(SetRingtoneDialogSubComponent::class))
abstract class SetRingtoneDialogInjector {

    @Binds
    @IntoMap
    @FragmentXKey(SetRingtoneDialog::class)
    internal abstract fun injectorFactory(builder: SetRingtoneDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
