package dev.olog.msc.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.DaggerFragment
import dev.olog.msc.analytics.AppAnalytics
import dev.olog.msc.utils.k.extension.isPortrait

abstract class BaseFragment : DaggerFragment(), LoggableFragment {

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    override fun onResume() {
        super.onResume()
        AppAnalytics.logScreen(activity, this)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun getSlidingPanel(): SlidingUpPanelLayout? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    protected fun isPortrait() : Boolean {
        return context != null && context!!.isPortrait
    }

}