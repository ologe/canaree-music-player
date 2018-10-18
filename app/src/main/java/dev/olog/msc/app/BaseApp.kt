package dev.olog.msc.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.squareup.leakcanary.LeakCanary
import dagger.android.*
import dev.olog.msc.dagger.base.HasAndroidXFragmentInjector
import javax.inject.Inject

abstract class BaseApp: Application(),
        HasActivityInjector,
        HasServiceInjector,
        HasBroadcastReceiverInjector,
        HasAndroidXFragmentInjector,
        DefaultLifecycleObserver {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Volatile private var needToInject = true

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        app = this
        injectIfNecessary()
        initializeApp()
    }

    protected abstract fun initializeApp()

    protected abstract fun applicationInjector(): AndroidInjector<out Application>

    private fun injectIfNecessary() {
        if (needToInject) {
            synchronized(this) {
                if (needToInject) {
                    val applicationInjector = applicationInjector() as AndroidInjector<Application>
                    applicationInjector.inject(this)
                    if (needToInject) {
                        throw IllegalStateException(
                                "The AndroidInjector returned from applicationInjector() did not inject the " + "DaggerApplication")
                    }
                }
            }
        }
    }

    @Inject internal fun setInjected() {
        needToInject = false
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return activityInjector
    }

    override fun broadcastReceiverInjector(): DispatchingAndroidInjector<BroadcastReceiver> {
        return broadcastReceiverInjector
    }

    override fun serviceInjector(): DispatchingAndroidInjector<Service> {
        return serviceInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}