package dev.olog.presentation.fragment_detail.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_detail.DetailFragment


@Subcomponent(modules = [
    (DetailFragmentModule::class),
    (DetailFragmentModuleSongs::class),
    (DetailFragmentModuleAlbum::class),
    (DetailFragmentModuleItem::class)
])
@PerFragment
interface DetailFragmentSubComponent : AndroidInjector<DetailFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailFragment>() {

        abstract fun detailModule(module: DetailFragmentModule): Builder

        override fun seedInstance(instance: DetailFragment) {
            detailModule(DetailFragmentModule(instance))
        }
    }

}
