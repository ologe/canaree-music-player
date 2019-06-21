package dev.olog.msc.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import dagger.android.support.AndroidSupportInjection
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment

abstract class BaseBottomSheetFragment : DimBottomSheetDialogFragment() {

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themeWrapper = ContextThemeWrapper(activity, activity!!.theme)
        val view = inflater.cloneInContext(themeWrapper).inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getArgument(key: String): T {
        return arguments!!.get(key) as T
    }

}