package dev.olog.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import dagger.android.support.DaggerFragment
import dev.olog.shared.isPortrait
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseFragment : DaggerFragment(), CoroutineScope by MainScope() {

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    protected fun isPortrait() : Boolean {
        return context != null && context!!.isPortrait
    }

}