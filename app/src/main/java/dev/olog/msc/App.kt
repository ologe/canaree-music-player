package dev.olog.msc

import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.data.DataConstants

class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        DataConstants.init(resources)


        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }
}
