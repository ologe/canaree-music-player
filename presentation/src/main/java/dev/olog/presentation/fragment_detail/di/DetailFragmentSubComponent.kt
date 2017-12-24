package dev.olog.presentation.fragment_detail.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.fragment_detail.most_player.di.DetailMostPlayedFragmentInjector
import dev.olog.presentation.fragment_detail.recently_added.di.DetailRecentlyAddedFragmentInjector


@Subcomponent(modules = arrayOf(
        DetailFragmentModule::class,
        DetailFragmentModuleSongs::class,
        DetailFragmentModuleAlbum::class,
        DetailFragmentModuleItem::class,

        DetailMostPlayedFragmentInjector::class,
        DetailRecentlyAddedFragmentInjector::class
))
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
