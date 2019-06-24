package dev.olog.image.provider.di

import dagger.Component
import dev.olog.image.provider.GlideModule
import dev.olog.injection.CoreComponent


@Component(dependencies = [CoreComponent::class])
@PerImageProvider
interface ImageProviderComponent {

    fun inject(instance: GlideModule)

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): ImageProviderComponent

    }

}