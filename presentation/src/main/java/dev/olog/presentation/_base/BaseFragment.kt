package dev.olog.presentation._base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.DaggerFragment
import dev.olog.presentation.HasSlidingPanel

abstract class BaseFragment : DaggerFragment() {

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = provideView(inflater, container, savedInstanceState)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    protected abstract fun provideView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View

    fun getSlidingPanel(): SlidingUpPanelLayout? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

}