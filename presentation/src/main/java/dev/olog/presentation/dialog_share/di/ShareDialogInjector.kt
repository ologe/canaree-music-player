package dev.olog.presentation.dialog_share.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.dialog_share.ShareDialog


@Module(subcomponents = arrayOf(ShareDialogSubComponent::class))
abstract class ShareDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(ShareDialog::class)
    internal abstract fun injectorFactory(builder: ShareDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
