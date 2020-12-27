package dev.olog.shared.android

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.shared.android.extensions.findActivity

val FragmentActivity.slidingPanel: BottomSheetBehavior<*>
    get() = (this as SlidingPanelAmbient).getSlidingPanel()

val Fragment.slidingPanel: BottomSheetBehavior<*>
    get() = requireActivity().slidingPanel

val View.slidingPanel: BottomSheetBehavior<*>
    get() = findActivity().slidingPanel

interface SlidingPanelAmbient {

    fun getSlidingPanel(): BottomSheetBehavior<*>

}
