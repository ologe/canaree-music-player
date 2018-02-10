package dev.olog.msc.presentation.dialog.set.ringtone.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog

@Module(subcomponents = arrayOf(SetRingtoneDialogSubComponent::class))
abstract class SetRingtoneDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(SetRingtoneDialog::class)
    internal abstract fun injectorFactory(builder: SetRingtoneDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
