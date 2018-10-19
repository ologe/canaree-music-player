package dev.olog.msc.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.DaggerFragment
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class BaseFragment : DaggerFragment() {

    private var disposable: Disposable? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        disposable = Single.timer(1, TimeUnit.SECONDS, Schedulers.computation())
                .subscribe({
                    // in case something went wrong, unlock postponed transition
                    startPostponedEnterTransition()
                }, Throwable::printStackTrace)
    }

    override fun onDetach() {
        super.onDetach()
        disposable.unsubscribe()
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)

        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun getSlidingPanel(): SlidingUpPanelLayout? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    protected fun isPortrait() : Boolean {
        return context != null && context!!.isPortrait
    }

}