package dev.olog.msc.presentation.library.folder.tree.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment

@Module(subcomponents = arrayOf(FolderTreeFragmentSubComponent::class))
abstract class FolderTreeFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(FolderTreeFragment::class)
    internal abstract fun injectorFactory(builder: FolderTreeFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
