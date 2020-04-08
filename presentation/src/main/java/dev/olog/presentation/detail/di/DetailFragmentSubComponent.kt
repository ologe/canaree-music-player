package dev.olog.presentation.detail.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.ScreenScope
import dev.olog.presentation.detail.DetailFragment


@Subcomponent(
    modules = arrayOf(
        DetailFragmentModule::class
    )
)
@ScreenScope
internal interface DetailFragmentSubComponent : AndroidInjector<DetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<DetailFragment> {

        override fun create(@BindsInstance instance: DetailFragment): DetailFragmentSubComponent
    }

}
