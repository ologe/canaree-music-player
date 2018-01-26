package dev.olog.msc

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ProcessLifecycleOwner
import com.google.firebase.perf.FirebasePerformance

object FirebasePerformance : DefaultLifecycleObserver {

    private var checkedAppStart = false

    private val realAppStartTrace by lazy {
        FirebasePerformance.getInstance().newTrace("realAppStart")
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun initialize(){}

    override fun onCreate(owner: LifecycleOwner) {
        realAppStartTrace.start()
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!checkedAppStart){
            checkedAppStart = true
            realAppStartTrace.stop()
        }
    }

}