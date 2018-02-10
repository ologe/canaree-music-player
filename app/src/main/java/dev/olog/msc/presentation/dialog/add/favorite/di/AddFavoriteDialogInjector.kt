package dev.olog.msc.presentation.dialog.add.favorite.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog


@Module(subcomponents = arrayOf(AddFavoriteDialogSubComponent::class))
abstract class AddFavoriteDialogInjector {

    @Binds
    @IntoMap
    @FragmentKey(AddFavoriteDialog::class)
    internal abstract fun injectorFactory(builder: AddFavoriteDialogSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
