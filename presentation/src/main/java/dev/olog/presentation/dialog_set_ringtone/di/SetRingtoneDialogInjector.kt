package dev.olog.presentation.dialog_set_ringtone.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_set_ringtone.SetRingtoneDialog

@Module(subcomponents = arrayOf(SetRingtoneDialogSubComponent::class))
abstract class SetRingtoneDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(SetRingtoneDialog::class)
    internal abstract fun injectorFactory(builder: SetRingtoneDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
