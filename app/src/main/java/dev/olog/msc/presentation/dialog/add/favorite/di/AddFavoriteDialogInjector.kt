package dev.olog.msc.presentation.dialog.add.favorite.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog


@Module(subcomponents = arrayOf(AddFavoriteDialogSubComponent::class))
abstract class AddFavoriteDialogInjector {

    @Binds
    @IntoMap
    @FragmentXKey(AddFavoriteDialog::class)
    internal abstract fun injectorFactory(builder: AddFavoriteDialogSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
