package dev.olog.msc.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.squareup.leakcanary.LeakCanary
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import dagger.android.support.DaggerApplication

abstract class BaseApp: DaggerApplication(),
        HasActivityInjector,
        HasServiceInjector,
        HasBroadcastReceiverInjector,
        DefaultLifecycleObserver {

    override fun onCreate() {
        super<DaggerApplication>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        app = this
        initializeApp()
    }

    protected abstract fun initializeApp()

}