package dev.olog.image.provider.di

import android.app.Application
import android.content.Context
import dev.olog.image.provider.GlideModule
import dev.olog.injection.CoreComponent

fun GlideModule.inject(context: Context){
    DaggerImageProviderComponent.factory()
        .create(CoreComponent.coreComponent(context.applicationContext as Application))
        .inject(this)
}