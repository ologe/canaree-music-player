package dev.olog.feature.base

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext

interface HasSlidingPanel {

    fun getPanel(): BottomSheetBehavior<*>

}

val View.slidingPanel: BottomSheetBehavior<*>
    get() = context.findInContext<HasSlidingPanel>().getPanel()

val Fragment.slidingPanel: BottomSheetBehavior<*>
    get() = requireActivity().slidingPanel

val FragmentActivity.slidingPanel: BottomSheetBehavior<*>
    get() = findInContext<HasSlidingPanel>().getPanel()