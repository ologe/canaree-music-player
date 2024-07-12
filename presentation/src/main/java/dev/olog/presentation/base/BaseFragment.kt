package dev.olog.presentation.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.android.support.DaggerFragment
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import kotlinx.coroutines.CoroutineScope

abstract class BaseFragment : DaggerFragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)
}

// todo move all below to common place
fun Fragment.getSlidingPanel(): MultiListenerBottomSheetBehavior<*>? {
    return (activity as HasSlidingPanel).getSlidingPanel()
}

fun Fragment.restoreUpperWidgetsTranslation(){
    (requireActivity() as MainActivity).restoreUpperWidgetsTranslation()
}

val Fragment.viewLifecycleScope: CoroutineScope
    get() = viewLifecycleOwner.lifecycleScope