package dev.olog.presentation.fragment_edit_info.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_edit_info.EditInfoFragment

@Module(subcomponents = arrayOf(EditInfoFragmentSubComponent::class))
abstract class EditInfoFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(EditInfoFragment::class)
    internal abstract fun injectorFactory(builder: EditInfoFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
