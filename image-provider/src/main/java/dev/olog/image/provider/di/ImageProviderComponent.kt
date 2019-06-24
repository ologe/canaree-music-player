package dev.olog.image.provider.di

import dev.olog.image.provider.GlideModule

//@Component(dependencies = [CoreComponent::class])
@PerImageProvider
interface ImageProviderComponent {

    fun inject(instance: GlideModule)

//    @Component.Factory
//    interface Factory {
//
//        fun create(component: CoreComponent): ImageProviderComponent
//
//    }

}