package dev.olog.msc.presentation.detail.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.detail.DetailFragment


@Subcomponent(modules = arrayOf(
        DetailFragmentModule::class,
        DetailFragmentModuleSongs::class,
        DetailFragmentModuleAlbum::class,
        DetailFragmentModuleItem::class,

        DetailFragmentModulePodcastAlbum::class,
        DetailFragmentModulePodcastItem::class
))
@PerFragment
interface DetailFragmentSubComponent : AndroidInjector<DetailFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailFragment>()

}
