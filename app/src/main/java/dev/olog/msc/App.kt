package dev.olog.msc

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.data.DataConstants

class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        DataConstants.init(resources)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }
}
