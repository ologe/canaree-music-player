package dev.olog.msc.presentation.base.adapter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.utils.assertMainThread
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class AdapterFirstEmission(lifecycle: Lifecycle): DefaultLifecycleObserver {

    private var alreadyEmitted = false

    /*
        If for any reason the adapter will never emit, automatically emit after a short amount of time.
        Sanity check if fragment has previously called postponeEnterTransition
     */
    private var disposable: Disposable = Single.timer(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ emitIfFirst() }, Throwable::printStackTrace)

    private var action : (() -> Unit)? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    fun setAction(action : (() -> Unit)?){
        this.action = action
    }

    fun emitIfFirst(){
        assertMainThread()

        if (!alreadyEmitted){
            alreadyEmitted = true
            action?.invoke()
        }
    }

}