package dev.olog.presentation.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import kotlinx.coroutines.CoroutineScope

@Deprecated("not needed")
abstract class BaseFragment : Fragment {

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

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.getArgument(key: String): T {
    return arguments!!.get(key) as T
}