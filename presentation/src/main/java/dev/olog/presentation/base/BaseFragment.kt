package dev.olog.presentation.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext
import kotlinx.coroutines.CoroutineScope

// todo move all below to common place
fun Fragment.getSlidingPanel(): MultiListenerBottomSheetBehavior<*>? {
    return (requireActivity().findInContext<HasSlidingPanel>()).getSlidingPanel()
}

fun Fragment.restoreUpperWidgetsTranslation(){
    (requireActivity() as MainActivity).restoreUpperWidgetsTranslation()
}

val Fragment.viewLifecycleScope: CoroutineScope
    get() = viewLifecycleOwner.lifecycleScope