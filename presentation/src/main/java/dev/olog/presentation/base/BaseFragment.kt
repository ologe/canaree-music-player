package dev.olog.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseFragment : Fragment(), CoroutineScope by MainScope() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>? {
        return (activity?.findInContext<HasSlidingPanel>())?.getSlidingPanel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancel()
    }

    fun restoreUpperWidgetsTranslation(){
        (requireActivity().findInContext<MainActivity>()).restoreUpperWidgetsTranslation()
    }

}