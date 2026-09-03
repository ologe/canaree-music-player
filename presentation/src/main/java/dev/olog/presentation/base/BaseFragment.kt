package dev.olog.presentation.base

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.shared.android.extensions.asType

abstract class BaseFragment : Fragment() {

    fun getSlidingPanel(): BottomSheetBehavior<*>? {
        return requireActivity().asType<HasSlidingPanel>().getSlidingPanel()
    }

    fun restoreUpperWidgetsTranslation(){
        requireActivity().asType<MainActivity>().restoreUpperWidgetsTranslation()
    }

}