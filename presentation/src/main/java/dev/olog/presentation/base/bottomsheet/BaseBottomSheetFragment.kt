package dev.olog.presentation.base.bottomsheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getArgument(key: String): T {
        return arguments!!.get(key) as T
    }

}